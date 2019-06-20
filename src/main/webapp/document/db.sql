-------------------------------------------------抽奖功能 begin
--奖品类型
DROP TYPE activity.award_type;
CREATE TYPE activity.award_type AS ENUM
    ('integral', 'custom_award', 'exist_award');
ALTER TYPE activity.award_type
    OWNER TO btfy;

--奖品状态
DROP TYPE activity.award_status;
CREATE TYPE activity.award_status AS ENUM
  ('no_receive', 'no_sent', 'has_sent', 'enter_order_process', 'enter_account');
ALTER TYPE activity.award_status
  OWNER TO btfy;

--奖品表
DROP TABLE activity.award;
CREATE TABLE activity.award (
  id              serial NOT NULL,
  lottery_id      integer NOT NULL,
  "type"          activity.award_type NOT NULL,
  probability     integer NOT NULL,
  amount          integer,
  ext             text,
  box_index       integer,
  remaind_amount  integer,
  "name"          varchar(30),
  image           varchar(1024),
  /* Keys */
  CONSTRAINT award_pkey
    PRIMARY KEY (id),
  /* Foreign keys */
  CONSTRAINT fk_lottery_id
    FOREIGN KEY (lottery_id)
    REFERENCES activity.lottery(id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.award
  OWNER TO btfy;
COMMENT ON COLUMN activity.award.lottery_id
  IS '关联的活动ID';
COMMENT ON COLUMN activity.award."type"
  IS '奖品类型  积分.库存商品.上传商品';
COMMENT ON COLUMN activity.award.probability
  IS '概率';
COMMENT ON COLUMN activity.award.amount
  IS '奖品数量';
COMMENT ON COLUMN activity.award.box_index
  IS '在前端的排序';
COMMENT ON COLUMN activity.award.remaind_amount
  IS '奖品剩余数量';
COMMENT ON COLUMN activity.award."name"
  IS '奖品名称';
COMMENT ON COLUMN activity.award.image
  IS '奖品图片';

--奖品日志表
DROP TABLE activity.award_log;
CREATE TABLE activity.award_log (
  log_id           bigserial NOT NULL,
  user_id          integer NOT NULL,
  lottery_name     varchar(32),
  award_type       activity.award_type NOT NULL,
  create_time      timestamp WITHOUT TIME ZONE NOT NULL,
  remote_ip        varchar(30),
  ext              text,
  lottery_id       integer,
  award_name       varchar(30),
  award_image      varchar(1024),
  province_id      integer,
  city_id          integer,
  county_id        integer,
  town_id          integer,
  receive_addr     varchar(256),
  remark           varchar(256),
  award_status     activity.award_status,
  receiver_name    varchar(30),
  receiver_phone   varchar(30),
  receiver_mobile  varchar(30),
  /* Keys */
  CONSTRAINT award_log_pkey
    PRIMARY KEY (log_id)
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.award_log
  OWNER TO btfy;

--抽奖活动表
--Table: activity.lottery
DROP TABLE activity.lottery;
CREATE TABLE activity.lottery (
  id                      serial NOT NULL PRIMARY KEY,
  lottery_name            varchar(30),
  need_integral           integer NOT NULL,
  limit_people            integer,
  begin_time              timestamp WITHOUT TIME ZONE,
  expire_time             timestamp WITHOUT TIME ZONE,
  remark                  varchar(4096),
  ext                     text,
  pc_publicity_image      varchar(1024),
  mobile_publicity_image  varchar(1024),
  is_delete               integer,
  /* Keys */
  CONSTRAINT lottery_pkey
    PRIMARY KEY (id)
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.lottery
  OWNER TO btfy;
COMMENT ON COLUMN activity.lottery.is_delete
  IS '1代表删去';

--增加订单类型
alter type orders.order_entry_source_type add value 'lottery';
-----------------------------------------------------抽奖功能 end



-----------------------------------------------------限时兑换，秒杀 begin
--活动状态
CREATE TYPE activity.flash_sale_activity_status AS ENUM
  ('will_do', 'be_doing', 'have_done');
ALTER TYPE activity.flash_sale_activity_status
  OWNER TO btfy;

--活动类型
CREATE TYPE activity.flash_sale_activity_type AS ENUM
  ('second_kill', 'limit_exchange');
ALTER TYPE activity.flash_sale_activity_type
  OWNER TO btfy;

--活动表
CREATE TABLE activity.flash_sale_activity (
  activity_name     varchar(256),
  products_num      integer,
  discount_rate     double precision,
  today_total_flow  bigint,
  start_time        timestamp WITHOUT TIME ZONE,
  end_time          timestamp WITHOUT TIME ZONE,
  id                bigserial NOT NULL,
  is_delete         integer,
  activity_status   activity.flash_sale_activity_status,
  activity_type     activity.flash_sale_activity_type
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.flash_sale_activity
  OWNER TO btfy;
COMMENT ON TABLE activity.flash_sale_activity
  IS '用于限时抢购,秒杀 活动数据';
COMMENT ON COLUMN activity.flash_sale_activity.activity_name
  IS '活动名称';
COMMENT ON COLUMN activity.flash_sale_activity.products_num
  IS '活动产品数量';
COMMENT ON COLUMN activity.flash_sale_activity.discount_rate
  IS '平均折扣';
COMMENT ON COLUMN activity.flash_sale_activity.today_total_flow
  IS '当天流量总数';
COMMENT ON COLUMN activity.flash_sale_activity.start_time
  IS '启动时间';
COMMENT ON COLUMN activity.flash_sale_activity.end_time
  IS '结束时间';
COMMENT ON COLUMN activity.flash_sale_activity.is_delete
  IS '1代表删去 ';
COMMENT ON COLUMN activity.flash_sale_activity.activity_type
  IS '活动类型';

--活动产品表
CREATE TABLE activity.flash_sale_product (
  id                      bigserial NOT NULL,
  activity_id             integer NOT NULL,
  product_id              integer NOT NULL,
  display_original_price  integer,
  display_discount_price  integer,
  discount_rate           double precision,
  limit_num               integer,
  buy_num_per_person      integer,
  start_time              timestamp WITHOUT TIME ZONE,
  end_time                timestamp WITHOUT TIME ZONE,
  remaind_amount          integer,
  /* Foreign keys */
  CONSTRAINT fk_product_id
    FOREIGN KEY (product_id)
    REFERENCES product.products(product_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.flash_sale_product
  OWNER TO btfy;
COMMENT ON TABLE activity.flash_sale_product
  IS '限时抢购,秒杀活动   产品数据';
COMMENT ON COLUMN activity.flash_sale_product.activity_id
  IS '关联的活动ID';
COMMENT ON COLUMN activity.flash_sale_product.product_id
  IS '产品ID';
COMMENT ON COLUMN activity.flash_sale_product.display_original_price
  IS '展示产品原价（可以跟真实原价不一样）';
COMMENT ON COLUMN activity.flash_sale_product.display_discount_price
  IS '展示产品折扣价格';
COMMENT ON COLUMN activity.flash_sale_product.discount_rate
  IS '展示折扣价格/展示原价';
COMMENT ON COLUMN activity.flash_sale_product.limit_num
  IS '限制总数';
COMMENT ON COLUMN activity.flash_sale_product.buy_num_per_person
  IS '每人限购数量';
COMMENT ON COLUMN activity.flash_sale_product.start_time
  IS '产品购买启动时间';
COMMENT ON COLUMN activity.flash_sale_product.end_time
  IS '产品购买结束时间';
COMMENT ON COLUMN activity.flash_sale_product.remaind_amount
  IS '剩余数量';
COMMENT ON CONSTRAINT fk_product_id ON activity.flash_sale_product
  IS '关联产品ID';

--活动订单日志
CREATE TABLE activity.flash_sale_order_log (
  id             bigserial NOT NULL,
  user_id        integer,
  order_id       bigint,
  fpid           integer,
  activity_type  activity.flash_sale_activity_type,
  buy_num        integer,
  create_time    timestamp WITHOUT TIME ZONE
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.flash_sale_order_log
  OWNER TO btfy;
COMMENT ON TABLE activity.flash_sale_order_log
  IS '限时兑换，秒杀 日志';
COMMENT ON COLUMN activity.flash_sale_order_log.user_id
  IS '用户ID';
COMMENT ON COLUMN activity.flash_sale_order_log.order_id
  IS '订单ID';
COMMENT ON COLUMN activity.flash_sale_order_log.fpid
  IS '限时兑换，限时秒杀产品ID关联flash_sale_product';
COMMENT ON COLUMN activity.flash_sale_order_log.activity_type
  IS '活动来源';
COMMENT ON COLUMN activity.flash_sale_order_log.buy_num
  IS '购买数量';

--
CREATE TABLE activity.activity_product_user (
  id           serial NOT NULL,
  activity_id  integer,
  user_id      integer,
  product_id   integer,
  /* Keys */
  CONSTRAINT activity_product_user_pkey
    PRIMARY KEY (id)
) WITH (
    OIDS = FALSE
  );
ALTER TABLE activity.activity_product_user
  OWNER TO btfy;

--增加订单类型
alter type orders.order_entry_source_type add value 'limit_exchange';
alter type orders.order_entry_source_type add value 'second_kill';
-----------------------------------------------------限时兑换，秒杀 end