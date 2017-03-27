DROP FUNCTION IF EXISTS `Goods_Stock`;
CREATE FUNCTION `Goods_Stock`(
  goodsId BIGINT
)
  RETURNS BIGINT(20) DETERMINISTIC BEGIN DECLARE result BIGINT;
  DECLARE goodsType VARCHAR(33);
  SELECT `DTYPE`
  INTO goodsType
  FROM `goods`
  WHERE `ID` = goodsId;
  IF goodsType = 'TicketGoods'
  THEN SELECT count(code.`CODE`)
       INTO result
       FROM `ticketcode` AS code LEFT JOIN `ticketbatch` AS batch ON code.`BATCH_ID` = batch.`ID`
       WHERE batch.`GOODS_ID` = goodsId AND code.`USED` = 0;
    RETURN result;
  ELSEIF goodsType = 'FakeGoods'
    THEN SELECT `STOCK`
         INTO result
         FROM `fakegoods`
         WHERE `ID` = goodsId;
      RETURN result;
  ELSE RETURN 0; END IF;
END;
