package com.bws.test;

import java.net.URL;

import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.ConfigurationManagerUtils;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;

/**
 * Encapsulates two ConfigurationManager instances, a "base" set and an
 * "override" set which supersedes the former.
 */
public class TwoTierConfigurationManager extends ConfigurationManager {

	public TwoTierConfigurationManager(ConfigurationManager baseCM) {
		super();
		this.baseCM = baseCM;
	}

	public TwoTierConfigurationManager(ConfigurationManager baseCM,
			String configFileName) throws PropertyException {
		super(configFileName);
		this.baseCM = baseCM;
		// TODO Auto-generated constructor stub
	}

	public TwoTierConfigurationManager(ConfigurationManager baseCM, URL url) {
		super(url);
		this.baseCM = baseCM;
		ConfigurationManagerUtils.configureLogger(this);
		// TODO Auto-generated constructor stub
	}

	/**
	 * The "base" configuration
	 */
	private ConfigurationManager baseCM;

	@Override
	public PropertySheet getPropertySheet(String instanceName) {
		PropertySheet ps = super.getPropertySheet(instanceName);
		if (null == ps && null != baseCM)
			ps = baseCM.getPropertySheet(instanceName);
		return ps;
	}

	@Override
	public String getGlobalProperty(String propertyName) {
		String s = super.getGlobalProperty(propertyName);
		if (null == s && null != baseCM)
			s = baseCM.getGlobalProperty(propertyName);
		return s;
	}

	@Override
	public String getGloPropReference(String propertyName) {
		String s = super.getGloPropReference(propertyName);
		if (null == s && null != baseCM)
			s = baseCM.getGloPropReference(propertyName);
		return s;
	}
}
