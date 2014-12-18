package com.bws.test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import edu.cmu.sphinx.util.props.ConfigurationManager;

public class RecognizerPool {

	private Map<URI, List<Resource>> resources = new HashMap<URI, List<Resource>>();
	private final ReentrantLock recognizerLock = new ReentrantLock(true);
	protected int defaultPoolSize = 1;
	protected String recognizerName;
	ConfigurationManager baseMgr;

	/**
	 * Constructor used to provide resource pool size
	 * 
	 * @param poolSize
	 *            The number of resources to manage
	 */
	public RecognizerPool(int poolSize, ConfigurationManager base,
			String recognizerName) {
		defaultPoolSize = poolSize;
		this.recognizerName = recognizerName;
		this.baseMgr = base;
	}

	/**
	 * Method used to initialize the resource pool
	 * 
	 * @param configuration
	 *            The configuration to use for Recognizer construction
	 * @param resourceSize
	 *            The number of Recognizers to manage
	 * @throws MalformedURLException
	 */
	private void initResource(URI configuration, int resourceSize)
			throws MalformedURLException {
		recognizerLock.lock();
		// Check for checked out
		if (resources.get(configuration) != null) {
			for (Resource checkResource : resources.get(configuration)) {
				if (checkResource.checkedOut) {
					throw new RuntimeException(
							String.format(
									"Attempted to initialize RasrRecognizers for URI %s with pending checkouts.",
									configuration));
				}
			}
		}

		resources.put(configuration, new ArrayList<Resource>(resourceSize));

		for (int i = 0; i < resourceSize; i++) {
			URL url = configuration.toURL();
			ConfigurationManager cm = new TwoTierConfigurationManager(baseMgr,
					url);

			StreamingRecognizer newRecognizer = (StreamingRecognizer) cm
					.lookup(recognizerName);
			newRecognizer.allocate();
			resources.get(configuration).add(new Resource(newRecognizer));
		}
		recognizerLock.unlock();
	}

	/**
	 * Method used to checkout a Recognizer
	 * 
	 * @param configuration
	 *            The configuration to be used for resource checkout
	 * @return Returns the next free Recognizer or null if no recognizers are
	 *         available for checkout
	 * @throws MalformedURLException
	 */
	public StreamingRecognizer checkout(URI configuration)
			throws MalformedURLException {
		recognizerLock.lock();

		if (!hasBeenInited(configuration)) {
			initResource(configuration, defaultPoolSize);
		}

		for (Resource checkResource : resources.get(configuration)) {
			if (!checkResource.checkedOut) {
				checkResource.checkedOut = true;
				recognizerLock.unlock();
				return checkResource.resource;
			}
		}

		recognizerLock.unlock();

		return null;
	}

	/**
	 * Method used to return a Recognizer back to the resource pool
	 * 
	 * @param configuration
	 *            The configuration to return the Recognizer
	 * @param resource
	 *            The Recognizer to be returned
	 * @throws UnrecognizedRecognizer
	 *             Thrown if the provided resource isn't found is the pool
	 *             configuration
	 */
	public void checkin(URI configuration, StreamingRecognizer resource)
			throws UnrecognizedRecognizer {
		recognizerLock.lock();
		for (Resource checkResource : resources.get(configuration)) {
			if (checkResource.resource == resource) {
				checkResource.checkedOut = false;
				recognizerLock.unlock();
				return;
			}
		}
		recognizerLock.unlock();
		throw new UnrecognizedRecognizer(
				"Attempted to checkin a RasrRecognizer that isn't managed.");

	}

	/**
	 * Exception used to indicate an attempt was made to access a Recognizer
	 * that isn't managed.
	 * 
	 * @author Bryan Staley
	 *
	 */
	public class UnrecognizedRecognizer extends Exception {
		/**
		 * Constructor
		 * 
		 * @param message
		 *            The error message
		 */
		public UnrecognizedRecognizer(String message) {
			super(message);
		}
	}

	/**
	 * General class to be used to manage Recognizers and their check in/out
	 * state
	 * 
	 * @author Bryan Staley
	 *
	 */
	private class Resource {
		boolean checkedOut;
		StreamingRecognizer resource;

		/**
		 * Constructor
		 * 
		 * @param recognizer
		 *            The Recognizer to be managed
		 */
		public Resource(StreamingRecognizer recognizer) {
			checkedOut = false;
			resource = recognizer;
		}

	}

	/**
	 * Method used to determine if a configuration has be initialized
	 * 
	 * @param configuration
	 *            The configuration to check
	 * @return True if the configuration has been initialized, false otherwise.
	 */
	private boolean hasBeenInited(URI configuration) {
		return resources.keySet().contains(configuration);
	}
}

/**
 * A ressource pool that will block until a resource is available.
 * 
 * @author Bryan Staley
 *
 */
class BlockingRecognizerPool extends RecognizerPool {

	/**
	 * Constructor
	 * 
	 * @param poolSize
	 *            The number of resources to manage
	 */
	public BlockingRecognizerPool(int poolSize, ConfigurationManager base,
			String recognizerName) {
		super(poolSize, base, recognizerName);
	}

	private Map<URI, Semaphore> resourceSemaphores = new HashMap<URI, Semaphore>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.redshift.rasr.SpeechRecognizer.RecognizerPool#checkout(java.net.URI)
	 */
	public StreamingRecognizer checkout(URI configuration)
			throws MalformedURLException {

		if (resourceSemaphores.get(configuration) == null) {
			resourceSemaphores.put(configuration,
					new Semaphore(defaultPoolSize));
		}
		try {
			resourceSemaphores.get(configuration).acquire();
		} catch (InterruptedException e) {
			// TODO
			e.printStackTrace();
			return null;
		}
		return super.checkout(configuration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.redshift.rasr.SpeechRecognizer.RecognizerPool#checkin(java.net.URI,
	 * com.redshift.rasr.sr.RasrRecognizer)
	 */
	public void checkin(URI configuration, StreamingRecognizer resource)
			throws UnrecognizedRecognizer {
		super.checkin(configuration, resource);
		resourceSemaphores.get(configuration).release();
	}

}
