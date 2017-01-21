DROP FUNCTION IF EXISTS `Goods_Stock`;
CREATE FUNCTION `Goods_Stock`(
  goodsId BIGINT
)
  RETURNS BIGINT(20) DETERMINISTIC BEGIN DECLARE result BIGINT;
  SELECT count(code.code)
  INTO result
  FROM ticketcode AS code LEFT JOIN ticketbatch AS batch ON code.batch_id = batch.id
  WHERE batch.goods_id = goodsId AND code.used = 0;
  RETURN result;
END;
