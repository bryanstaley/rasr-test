'''
Created on Feb 16, 2017

@author: bstaley
'''


from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, DateTime

#db_uri = 'mysql+mysqldb://rasr:password@173.194.240.114/rasr'
db_uri = 'mysql+mysqldb://rasr:password@127.0.0.1:3306/rasr'

engine = create_engine(db_uri,
                       convert_unicode=True,
                       echo=False,
                        echo_pool=False,
                        pool_size=1,
                        max_overflow=1)
db_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=engine))

Base = declarative_base()
Base.query = db_session.query_property()


class RasrTransaction(Base):
    
    __tablename__='transaction'
    
    id = Column(Integer, primary_key=True,nullable=False)
    siteId = Column(Integer,nullable=False)
    start = Column(DateTime(timezone=False),nullable=False)
    end = Column(DateTime(timezone=False),nullable=False)
    audioQuality = Column(String)
    audioFormat = Column(String)
    bitSize = Column(Integer)
    clientIp = Column(String)
    rasrInstanceHostname = Column(String)
    audioFileName = Column(String)
    audioFileLoc = Column(String)
    userAgent = Column(String)
    
    def __init__(self,
                 siteId,
                 start,
                 end,
                 audioQuality=None,
                 audioFormat=None,
                 bitSize=None,
                 clientIp=None,
                 rasrInstanceHostname=None,
                 audioFileName=None,
                 audioFileLoc=None,
                 userAgent=None):
        self.siteId = siteId
        self.start=start
        self.end=end
        self.audioQuality=audioQuality
        self.audioFormat=audioFormat
        self.bitSize = bitSize
        self.clientIp = clientIp
        self.rasrInstanceHostname = rasrInstanceHostname
        self.audioFileName = audioFileName
        self.audioFileLoc = audioFileLoc
        self.userAgent = userAgent


def record_transaction(
                       siteId,
                       start,
                       end,
                       audioQuality=None,
                       audioFormat=None,
                       bitSize=None,
                       clientIp=None,
                       rasrInstanceHostname=None,
                       audioFileName=None,
                       audioFileLoc=None,
                       userAgent=None):
    
    new_transaction = RasrTransaction(
                                      siteId=siteId,
                                      start=start,
                                      end=end,
                                      audioQuality=audioQuality,
                                      audioFormat=audioFormat,
                                      bitSize=bitSize,
                                      clientIp=clientIp,
                                      rasrInstanceHostname=rasrInstanceHostname,
                                      audioFileName=audioFileName,
                                      audioFileLoc=audioFileLoc,
                                      userAgent=userAgent)
    db_session.add(new_transaction)
    db_session.commit()
    
