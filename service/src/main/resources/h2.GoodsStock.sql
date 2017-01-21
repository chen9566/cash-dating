SELECT count(code.code)
FROM ticketcode AS code LEFT JOIN ticketbatch AS batch ON code.batch_id = batch.id
WHERE batch.goods_id = ? AND code.used = 0