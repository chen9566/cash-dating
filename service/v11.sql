ALTER TABLE cash.cashorder
  ADD BOOKRATE DECIMAL(10, 7) DEFAULT 0;
ALTER TABLE cash.CASHORDER
  ADD WITHDRAWALCOMPLETED TINYINT(1) DEFAULT 0;


INSERT INTO userorder SELECT
                        ID,
                        'CashOrder',
                        AMOUNT,
                        COMMENT,
                        STARTTIME,
                        OWNER_ID
                      FROM cashorder;


ALTER TABLE cash.cashorder
  DROP FOREIGN KEY FK_CASHORDER_OWNER_ID;
DROP INDEX FK_CASHORDER_OWNER_ID ON cash.cashorder;
ALTER TABLE cashorder
  DROP OWNER_ID;
ALTER TABLE cashorder
  DROP AMOUNT;
ALTER TABLE cashorder
  DROP COMMENT;
ALTER TABLE cashorder
  DROP STARTTIME;


ALTER TABLE cash.withdraworder
  DROP FOREIGN KEY FK_WITHDRAWORDER_OWNER_ID;
DROP INDEX FK_WITHDRAWORDER_OWNER_ID ON withdraworder;
ALTER TABLE withdraworder
  DROP OWNER_ID;
ALTER TABLE withdraworder
  DROP AMOUNT;
ALTER TABLE withdraworder
  DROP COMMENT;
ALTER TABLE withdraworder
  DROP STARTTIME;


ALTER TABLE card
  MODIFY NUMBER VARCHAR(30) NOT NULL;
ALTER TABLE platformwithdrawalorder
  MODIFY NUMBER VARCHAR(30) NOT NULL;