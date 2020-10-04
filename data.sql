-- DELETE FROM VOTER;
-- DELETE FROM APP_USER;
-- DELETE FROM GUEST;
-- DELETE FROM VOTE;
-- DELETE FROM POLL;
-- DELETE FROM VOTING_DEVICE;
-- DELETE FROM DISPLAY_DEVICE;



-- -----------------------------------------------------
-- Table VOTER
-- -----------------------------------------------------
INSERT INTO TEST.VOTER (ID, USERNAME)
VALUES (1, 'Bob'),
       (2,  'Kåre'),
       (101, 'Guest 101'),
       (102, 'Guest 102');

-- -----------------------------------------------------
-- Table APP_USER
-- -----------------------------------------------------
INSERT INTO TEST.APP_USER(IS_ADMIN, PASSWORD, ID)
VALUES (true, '1234', 1);
VALUES (true, 'ost', 2);

-- -----------------------------------------------------
-- Table GUEST
-- -----------------------------------------------------
INSERT INTO TEST.GUEST (ID)
VALUES (101),
       (102);

-- -----------------------------------------------------
-- Table POLL
-- -----------------------------------------------------
INSERT INTO TEST.POLL(ID, NAME, POLL_DURATION, QUESTION, START_TIME, VISIBILITY_TYPE,
                                    POLL_ID)
VALUES (1679616, 'pinapple', 100, 'pinapple on pizza?', '2020-10-04 22:48:57.750000000', 'PUBLIC',1),
       (1679617, 'Cats vs dogs', 100, 'Cats better than dogs?', '2020-10-02 22:48:57.750000000', 'PRIVATE', 2),
       (1679618, 'Vue > React', 100, 'Vue > React', '2020-10-01 22:48:57.750000000', 'PUBLIC', 1);
