PRAGMA foreign_keys = false;

-- ----------------------------
--  Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS "account";
CREATE TABLE "account" (
	 "_id" INTEGER,
	 "stable_id" TEXT NOT NULL UNIQUE,
	 "relogin_info" TEXT NOT NULL,
	 "logged_in" INTEGER DEFAULT 0,
	PRIMARY KEY("_id")
);

-- ----------------------------
--  Table structure for associated_identity
-- ----------------------------
DROP TABLE IF EXISTS "associated_identity";
CREATE TABLE "associated_identity" (
	 "_id" INTEGER PRIMARY KEY,
	 "identity_provider_id" integer NOT NULL,
	 "identity_uri" TEXT NOT NULL UNIQUE,
	 "account_id" integer,
	 "self_contact_id" INTEGER NOT NULL UNIQUE,
	 "downloaded_contacts_version" TEXT,
	CONSTRAINT "fk_identity_account_id" FOREIGN KEY ("account_id") REFERENCES "account" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_identity_provider_id" FOREIGN KEY ("identity_provider_id") REFERENCES "identity_provider" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_identity_profile_id" FOREIGN KEY ("self_contact_id") REFERENCES "rolodex_contact" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for avatar
-- ----------------------------
DROP TABLE IF EXISTS "avatar";
CREATE TABLE "avatar" (
	 "_id" INTEGER primary key,
	 "rolodex_id" integer,
	 "name" TEXT,
	 "avatar_uri" TEXT NOT NULL,
	 "width" INTEGER,
	 "height" INTEGER,
	CONSTRAINT "fk_avatars_rolodex_id" FOREIGN KEY ("rolodex_id") REFERENCES "rolodex_contact" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "uni-avatars-rolodex-uri" UNIQUE ("rolodex_id","avatar_uri")
);

-- ----------------------------
--  Table structure for call
-- ----------------------------
DROP TABLE IF EXISTS "call";
CREATE TABLE "call" (
	 "_id" INTEGER PRIMARY KEY,
	 "call_id" TEXT NOT NULL UNIQUE,
	 "type" TEXT NOT NULL,
	 "peer_id" INTEGER NOT NULL,
	 "direction" integer NOT NULL DEFAULT 0,
	 "time" integer,
	 "answer_time" integer default 0,
	 "end_time" integer default 0,
	 "conversation_id" TEXT NOT NULL,
	 "conversation_event_id" long,
	 "cbc_id" INTEGER,
	CONSTRAINT "fk_call_conversation_id" FOREIGN KEY ("conversation_id") REFERENCES "conversation" ("conversation_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_call_caller_id" FOREIGN KEY ("peer_id") REFERENCES "openpeer_contact" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for call_event
-- ----------------------------
DROP TABLE IF EXISTS "call_event";
CREATE TABLE "call_event" (
	 "_id" INTEGER PRIMARY KEY,
	 "call_id" TEXT NOT NULL,
	 "event" TEXT NOT NULL,
	 "time" integer NOT NULL,
	CONSTRAINT "fk_call_event_call_id" FOREIGN KEY ("call_id") REFERENCES "call" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS "conversation";
CREATE TABLE "conversation" (
	 "_id" INTEGER PRIMARY KEY,
	 "type" TEXT NOT NULL,
	 "name" TEXT,
	 "topic" TEXT,
	 "conversation_id" TEXT UNIQUE NOT NULL,
	 "account_id" integer NOT NULL,
	 "start_time" integer NOT NULL,
	 "participants" integer NOT NULL,
	 "removed" integer DEFAULT 0,
	 "quit" integer DEFAULT 0,
	CONSTRAINT "fk_conversation_account_id" FOREIGN KEY ("account_id") REFERENCES "account" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_conversation_participants" FOREIGN KEY ("participants") REFERENCES "participants" ("cbc_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for conversation_event
-- ----------------------------
DROP TABLE IF EXISTS "conversation_event";
CREATE TABLE "conversation_event" (
	 "_id" INTEGER PRIMARY KEY,
	 "event" TEXT NOT NULL,
	 "content" TEXT,
	 "time" integer NOT NULL,
	 "participants" INTEGER,
	 "conversation_id" TEXT NOT NULL,

	CONSTRAINT "fk_conversation_event_conversation_id" FOREIGN KEY ("conversation_id") REFERENCES "conversation" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_conversation_event_participants" FOREIGN KEY ("participants") REFERENCES "participants" ("cbc_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for identity_contact
-- ----------------------------
DROP TABLE IF EXISTS "identity_contact";
CREATE TABLE "identity_contact" (
	 "_id" INTEGER PRIMARY KEY,
	 "identity_proof_bundle" TEXT NOT NULL,
	 "priority" INTEGER,
	 "weight" INTEGER,
	 "last_update_time" INTEGER,
	 "expire" integer
);

-- ----------------------------
--  Table structure for identity_provider
-- ----------------------------
DROP TABLE IF EXISTS "identity_provider";
CREATE TABLE "identity_provider" (
	 "_id" INTEGER PRIMARY KEY,
	 "domain" TEXT NOT NULL
);

-- ----------------------------
--  Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS "message";
CREATE TABLE "message" (
	 "_id" INTEGER PRIMARY KEY,
	 "message_id" TEXT NOT NULL UNIQUE,
	 "cbc_id" INTEGER,
	 "conversation_id" integer,
	 "conversation_event_id" INTEGER,
	 "type" TEXT NOT NULL,
	 "sender_id" INTEGER NOT NULL,
	 "text" TEXT,
	 "time" INTEGER NOT NULL,
	 "read" integer,
	 "incoming_message_status" INTEGER NOT NULL DEFAULT 0,
	 "outgoing_message_status" TEXT,
	 "edit_status" integer DEFAULT 0,
	CONSTRAINT "fk_message_conversation_id" FOREIGN KEY ("conversation_id") REFERENCES "conversation" ("conversation_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_message_cbc_id" FOREIGN KEY ("cbc_id") REFERENCES "participants" ("cbc_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_message_sender_id" FOREIGN KEY ("sender_id") REFERENCES "openpeer_contact" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_message_conversation_event_id" FOREIGN KEY ("conversation_event_id") REFERENCES "conversation_event" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for message_event
-- ----------------------------
DROP TABLE IF EXISTS "message_event";
CREATE TABLE "message_event" (
	 "_id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	 "message_id" integer NOT NULL,
	 "event" TEXT NOT NULL,
	 "description" TEXT,
	 "time" INTEGER NOT NULL,

	CONSTRAINT "fk_message_change_record_message_id" FOREIGN KEY ("message_id") REFERENCES "message" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for openpeer_contact
-- ----------------------------
DROP TABLE IF EXISTS "openpeer_contact";
CREATE TABLE "openpeer_contact" (
	 "_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	 "stable_id" text NOT NULL UNIQUE,
	 "peer_uri" TEXT NOT NULL UNIQUE,
	 "peerfile_public" TEXT NOT NULL UNIQUE
);

-- ----------------------------
--  Table structure for participants
-- ----------------------------
DROP TABLE IF EXISTS "participants";
CREATE TABLE "participants" (
	 "_id" INTEGER PRIMARY KEY,
	 "cbc_id" INTEGER,
	 "openpeer_contact_id" INTEGER,
	CONSTRAINT "fk_participants_profile_id" FOREIGN KEY ("openpeer_contact_id") REFERENCES "openpeer_contact" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for peerfile_public
-- ----------------------------
DROP TABLE IF EXISTS "peerfile_public";
CREATE TABLE "peerfile_public" (
	 "_id" INTEGER PRIMARY KEY,
	 "peer_uri" TEXT NOT NULL UNIQUE,
	 "peerfile_public" TEXT
);

-- ----------------------------
--  Table structure for rolodex_contact
-- ----------------------------
DROP TABLE IF EXISTS "rolodex_contact";
CREATE TABLE "rolodex_contact" (
	 "_id" INTEGER PRIMARY KEY AUTOINCREMENT,
	 "associated_identity_id" INTEGER DEFAULT 0,
	 "name" TEXT NOT NULL COLLATE NOCASE,
	 "identity_provider_id" integer NOT NULL,
	 "identity_uri" TEXT NOT NULL UNIQUE,
	 "profile_url" TEXT,
	 "vprofile_url" TEXT,
	 "readyForDeletion" integer,
	 "openpeer_contact_id" integer DEFAULT 0,
	 "identity_contact_id" INTEGER DEFAULT 0,
	 "is_primary" integer default 1,
	CONSTRAINT "fk_rolodex_identity_provider_id" FOREIGN KEY ("identity_provider_id") REFERENCES "identity_provider" ("_id") ON DELETE RESTRICT,
	CONSTRAINT "fk_rolodex_contact_associated_identity_id" FOREIGN KEY ("associated_identity_id") REFERENCES "associated_identity" ("_id") ON DELETE RESTRICT
);

-- ----------------------------
--  Table structure for thread
-- ----------------------------
DROP TABLE IF EXISTS "thread";
CREATE TABLE "thread" (
	 "_id" INTEGER PRIMARY KEY,
	 "thread_id" TEXT NOT NULL UNIQUE,
	 "start_time" INTEGER NOT NULL,
	 "conversation_id" INTEGER,
	CONSTRAINT "fk_thread_conversation_id" FOREIGN KEY ("conversation_id") REFERENCES "conversation" ("_id")
);

-- ----------------------------
--  Indexes structure for table rolodex_contact
-- ----------------------------
CREATE INDEX "idx_rolodex_identity_uri" ON rolodex_contact ("identity_uri");
CREATE INDEX "idx_rolodex_openpeer_id" ON rolodex_contact ("openpeer_contact_id");
CREATE INDEX "idx_rolodex_associated_identity_id" ON rolodex_contact ("associated_identity_id");

-- ----------------------------
--  Indexes structure for table avatar
-- ----------------------------
CREATE INDEX "idx_avatar_rolodex_id" ON avatar ("rolodex_id");

-- ----------------------------
--  Indexes structure for table call
-- ----------------------------
CREATE INDEX "idx_call_peer_id" ON call ("peer_id" );
CREATE INDEX "idx_call_direction" ON call ("direction" );
CREATE INDEX "idx_call_cbc_id" ON call ("cbc_id" );
CREATE INDEX "idx_call_conversation_id" ON call ("conversation_id" );
CREATE INDEX "idx_call_conversation_event_id" ON call ("conversation_event_id" );

-- ----------------------------
--  Indexes structure for table call_event
-- ----------------------------
CREATE INDEX "idx_call_event_call_id" ON call_event ("call_id");
CREATE INDEX "idx_call_event_event" ON call_event ("event");
CREATE INDEX "idx_call_event_time" ON call_event ("time");



-- ----------------------------
--  Indexes structure for table conversation_event
-- ----------------------------
CREATE INDEX "idx_conversation_event_participants_id" ON conversation_event ("participants");
CREATE INDEX "idx_conversation_event_conversation_id" ON conversation_event ("conversation_id");

-- ----------------------------
--  Indexes structure for table message
-- ----------------------------
CREATE INDEX "idx_message_cbc_id" ON message ("cbc_id" );
CREATE INDEX "idx_message_conversation_id" ON message ("conversation_id" );
CREATE INDEX "idx_message_time" ON message ("time" );


-- ----------------------------
--  Indexes structure for table message_event
-- ----------------------------
CREATE INDEX "idx_message_change_record_message_id" ON message_event ("message_id" );

-- ----------------------------
--  Indexes structure for table participants
-- ----------------------------
CREATE INDEX "idx_participants_cbc_id" ON participants ("cbc_id" );

-- ----------------------------
--  Indexes structure for table peerfile_public
-- ----------------------------
CREATE UNIQUE INDEX "idx_peerfile_public_peer_uri" ON peerfile_public ("peer_uri");

-- ----------------------------
--  Indexes structure for table identity_contact
-- ----------------------------
CREATE  INDEX "idx_identity_contact_priority" ON identity_contact ("priority");
CREATE  INDEX "idx_identity_contact_weight" ON identity_contact ("weight");

-- ----------------------------
--  Indexes structure for table associated_identity
-- ----------------------------
CREATE  INDEX "idx_associated_identity_account_id" ON associated_identity ("account_id");
CREATE  INDEX "idx_associated_identity_identity_uri" ON associated_identity ("identity_uri");

PRAGMA foreign_keys = true;
