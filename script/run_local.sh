set -x

SPARK_PARAMS='--master local[4]'
APP_JAR=target/scala-2.10/zbirch.jar
DATA_DIR=.
LOG_DIR=./log

# noise

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_4c_0.0n.data \
    --labels $DATA_DIR/100000p_2d_4c_0.0n.data_labels --k 4 --pars 8 ; sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_4c_0.01n.data \
    --labels $DATA_DIR/100000p_2d_4c_0.01n.data_labels --k 4 --pars 8 ; sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_4c_0.05n.data \
    --labels $DATA_DIR/100000p_2d_4c_0.05n.data_labels --k 4 --pars 8 ; sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_4c_0.1n.data \
    --labels $DATA_DIR/100000p_2d_4c_0.1n.data_labels --k 4 --pars 8 ; sleep 3

# K

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_2d_16c_0.05n.data_labels --k 16 --pars 8 ; sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_64c_0.05n.data \
    --labels $DATA_DIR/100000p_2d_64c_0.05n.data_labels --k 64 --pars 8 ; sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_2d_128c_0.05n.data \
    --labels $DATA_DIR/100000p_2d_128c_0.05n.data_labels --k 128 --pars 8 ; sleep 3

# dimension

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_4c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_4c_0.05n.data_labels --k 4 --pars 8 ; sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_16d_4c_0.05n.data \
    --labels $DATA_DIR/100000p_16d_4c_0.05n.data_labels --k 4 --pars 8 ; sleep 3

# number of partitions (trees) (for accuracy)

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.1p.log \
    --k 16 --pars 1

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.2p.log \
    --k 16 --pars 2

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.4p.log \
    --k 16 --pars 4

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.8p.log \
    --k 16 --pars 8

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.12p.log \
    --k 16 --pars 12

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.16p.log \
    --k 16 --pars 16

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.20p.log \
    --k 16 --pars 20

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.24p.log \
    --k 16 --pars 24

sleep 3

# memory limit affect on performance and accuracy

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.8m.log \
    --k 16 --pars 8 --memory 8000000 # 8M

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.16m.log \
    --k 16 --pars 8 --memory 16000000 # 16M

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.32m.log \
    --k 16 --pars 8 --memory 32000000 # 32M

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.64m.log \
    --k 16 --pars 8 --memory 64000000 # 64M

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.128m.log \
    --k 16 --pars 8 --memory 128000000 # 128M

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.256m.log \
    --k 16 --pars 8 --memory 256000000 # 256M

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.512m.log \
    --k 16 --pars 8 --memory 512000000 # 512M

sleep 3

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.1g.log \
    --k 16 --pars 8 --memory 1000000000 # 1G

sleep 3

# number of total executor cores (for performance)

spark-submit --master local[1] $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.1c.log \
    --k 16 --pars 8

sleep 3

spark-submit --master local[2] $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.2c.log \
    --k 16 --pars 8

sleep 3

spark-submit --master local[3] $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.3c.log \
    --k 16 --pars 8

sleep 3

spark-submit --master local[4] $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data \
    --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels \
    --log $LOG_DIR/100000p_8d_16c_0.05n.4c.log \
    --k 16 --pars 8

sleep 3

# number of points scale
# ...
