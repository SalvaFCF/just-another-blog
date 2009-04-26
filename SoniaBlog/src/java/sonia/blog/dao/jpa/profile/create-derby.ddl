
CREATE TABLE ATTACHMENT (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, FILESIZE BIGINT NOT NULL, CREATIONDATE TIMESTAMP NOT NULL, MIMETYPE VARCHAR(255) NOT NULL, DESCRIPTION VARCHAR(255), NAME VARCHAR(255) NOT NULL, FILEPATH VARCHAR(255) NOT NULL, ENTRY_ID BIGINT, PAGE_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE ENTRY_CATEGORY (entries_ID BIGINT NOT NULL, categories_ID BIGINT NOT NULL, PRIMARY KEY (entries_ID, categories_ID))
CREATE TABLE ENTRY_TAG (entries_ID BIGINT NOT NULL, tags_ID BIGINT NOT NULL, PRIMARY KEY (entries_ID, tags_ID))
CREATE TABLE COMMENT (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, AUTHORADDRESS VARCHAR(255), AUTHORMAIL VARCHAR(255), CONTENT VARCHAR(2048) NOT NULL, AUTHORURL VARCHAR(255), AUTHORNAME VARCHAR(255) NOT NULL, SPAM SMALLINT DEFAULT 0, CREATIONDATE TIMESTAMP NOT NULL, ENTRY_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE BlogUser (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, PASSWORD VARCHAR(255) NOT NULL, ACTIVATIONCODE VARCHAR(255) NOT NULL, EMAIL VARCHAR(255) UNIQUE NOT NULL, REGISTRATIONDATE TIMESTAMP NOT NULL, NAME VARCHAR(255) UNIQUE NOT NULL, GLOBALADMIN SMALLINT DEFAULT 0, LASTLOGIN TIMESTAMP, SELFMANAGED SMALLINT DEFAULT 0, DISPLAYNAME VARCHAR(255) NOT NULL, ACTIVE SMALLINT DEFAULT 0, PRIMARY KEY (ID))
CREATE TABLE BLOGMEMBER (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, BlogRole INTEGER NOT NULL, REGISTRATIONDATE TIMESTAMP NOT NULL, BLOG_ID BIGINT, USER_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE TAG (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, NAME VARCHAR(255) UNIQUE NOT NULL, PRIMARY KEY (ID))
CREATE TABLE ENTRY (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, PUBLISHED SMALLINT DEFAULT 0, ALLOWCOMMENTS SMALLINT DEFAULT 0, TEASER VARCHAR(5000), PUBLISHINGDATE TIMESTAMP, CREATIONDATE TIMESTAMP NOT NULL, TITLE VARCHAR(255) NOT NULL, LASTUPDATE TIMESTAMP, CONTENT CLOB(64000), BLOG_ID BIGINT, AUTHOR_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE BLOGHITCOUNT (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, date DATE NOT NULL, HITCOUNT BIGINT, BLOG_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE BLOG (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, DESCRIPTION VARCHAR(255), EMAIL VARCHAR(255), TITLE VARCHAR(250) NOT NULL, ENTRIESPERPAGE INTEGER, CREATIONDATE TIMESTAMP NOT NULL, IMAGEHEIGHT INTEGER, TEMPLATE VARCHAR(255) NOT NULL, IMAGEWIDTH INTEGER, ALLOWCOMMENTS SMALLINT DEFAULT 0, LOCALE VARCHAR(255), ALLOWMACROS SMALLINT DEFAULT 0, THUMBNAILHEIGHT INTEGER, DATEFORMAT VARCHAR(50) NOT NULL, THUMBNAILWIDTH INTEGER, ACTIVE SMALLINT DEFAULT 0, TIMEZONE VARCHAR(255), ALLOWHTMLCOMMENTS SMALLINT DEFAULT 0, IDENTIFIER VARCHAR(255) UNIQUE NOT NULL, PRIMARY KEY (ID))
CREATE TABLE CATEGORY (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, CREATIONDATE TIMESTAMP NOT NULL, DESCRIPTION VARCHAR(255), NAME VARCHAR(255) NOT NULL, BLOG_ID BIGINT, PRIMARY KEY (ID))
CREATE TABLE PAGE (ID BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL, PUBLISHINGDATE TIMESTAMP, NAVIGATIONPOSITION INTEGER, NAVIGATIONTITLE VARCHAR(255) NOT NULL, PUBLISHED SMALLINT DEFAULT 0, CREATIONDATE TIMESTAMP NOT NULL, CONTENT CLOB(64000), TITLE VARCHAR(255) NOT NULL, LASTUPDATE TIMESTAMP, AUTHOR_ID BIGINT, BLOG_ID BIGINT, PARENT_ID BIGINT, PRIMARY KEY (ID))

# ALTER TABLE BLOGMEMBER ADD CONSTRAINT UNQ_BLOGMEMBER_0 UNIQUE (USER, BLOG)
# ALTER TABLE BLOGHITCOUNT ADD CONSTRAINT UNQ_BLOGHITCOUNT_0 UNIQUE (DATE, BLOG)
# ALTER TABLE CATEGORY ADD CONSTRAINT UNQ_CATEGORY_0 UNIQUE (NAME, BLOG)
ALTER TABLE ATTACHMENT ADD CONSTRAINT ATTACHMENT_PAGE_ID FOREIGN KEY (PAGE_ID) REFERENCES PAGE (ID)
ALTER TABLE ATTACHMENT ADD CONSTRAINT ATTACHMENTENTRY_ID FOREIGN KEY (ENTRY_ID) REFERENCES ENTRY (ID)
ALTER TABLE ENTRY_CATEGORY ADD CONSTRAINT NTRYCTGORYctgresID FOREIGN KEY (categories_ID) REFERENCES CATEGORY (ID)
ALTER TABLE ENTRY_CATEGORY ADD CONSTRAINT NTRYCATEGORYntrsID FOREIGN KEY (entries_ID) REFERENCES ENTRY (ID)
ALTER TABLE ENTRY_TAG ADD CONSTRAINT ENTRYTAGentries_ID FOREIGN KEY (entries_ID) REFERENCES ENTRY (ID)
ALTER TABLE ENTRY_TAG ADD CONSTRAINT ENTRY_TAG_tags_ID FOREIGN KEY (tags_ID) REFERENCES TAG (ID)
ALTER TABLE COMMENT ADD CONSTRAINT COMMENT_ENTRY_ID FOREIGN KEY (ENTRY_ID) REFERENCES ENTRY (ID)
ALTER TABLE BLOGMEMBER ADD CONSTRAINT BLOGMEMBER_BLOG_ID FOREIGN KEY (BLOG_ID) REFERENCES BLOG (ID)
ALTER TABLE BLOGMEMBER ADD CONSTRAINT BLOGMEMBER_USER_ID FOREIGN KEY (USER_ID) REFERENCES BlogUser (ID)
ALTER TABLE ENTRY ADD CONSTRAINT FK_ENTRY_BLOG_ID FOREIGN KEY (BLOG_ID) REFERENCES BLOG (ID)
ALTER TABLE ENTRY ADD CONSTRAINT FK_ENTRY_AUTHOR_ID FOREIGN KEY (AUTHOR_ID) REFERENCES BlogUser (ID)
ALTER TABLE BLOGHITCOUNT ADD CONSTRAINT BLOGHITCOUNTBLOGID FOREIGN KEY (BLOG_ID) REFERENCES BLOG (ID)
ALTER TABLE CATEGORY ADD CONSTRAINT CATEGORY_BLOG_ID FOREIGN KEY (BLOG_ID) REFERENCES BLOG (ID)
ALTER TABLE PAGE ADD CONSTRAINT FK_PAGE_PARENT_ID FOREIGN KEY (PARENT_ID) REFERENCES PAGE (ID)
ALTER TABLE PAGE ADD CONSTRAINT FK_PAGE_BLOG_ID FOREIGN KEY (BLOG_ID) REFERENCES BLOG (ID)
ALTER TABLE PAGE ADD CONSTRAINT FK_PAGE_AUTHOR_ID FOREIGN KEY (AUTHOR_ID) REFERENCES BlogUser (ID)