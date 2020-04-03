-- Database: firebird
-- Change Parameter: existingColumnName=state
-- Change Parameter: existingTableName=address
-- Change Parameter: newColumnName=abbreviation
-- Change Parameter: newTableName=state
CREATE TABLE state AS SELECT DISTINCT state AS abbreviation FROM address WHERE state IS NOT NULL;
UPDATE RDB$RELATION_FIELDS SET RDB$NULL_FLAG = 1 WHERE RDB$RELATION_NAME = 'state' AND RDB$FIELD_NAME = 'abbreviation';
ALTER TABLE state ADD PRIMARY KEY (abbreviation);
ALTER TABLE address ADD CONSTRAINT FK_ADDRESS_STATE FOREIGN KEY (state) REFERENCES state (abbreviation);
