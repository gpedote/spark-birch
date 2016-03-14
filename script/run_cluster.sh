SPARK_PARAMS='--master spark://SCAI01.CS.UCLA.EDU:7077'
APP_JAR='/home/clash/libs/zbirch.jar'
DATA_DIR='hdfs://scai01.cs.ucla.edu:9000/clash/datasets/shuffled_data'
LOG_DIR='/home/clash/sparks/spark-1.5.1/logs'

NUM_MACHINE=1  # do with 1, 2, 4, 8, 15 number of machines

# Study of scalability w.r.t number of points

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/100000p_8d_16c_0.05n.data --labels $DATA_DIR/100000p_8d_16c_0.05n.data_labels --log "$LOG_DIR/100000p_8d_16c_0.05n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )) ; sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/1000000p_8d_16c_0.05n.data --labels $DATA_DIR/1000000p_8d_16c_0.05n.data_labels --log "$LOG_DIR/1000000p_8d_16c_0.05n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )); sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_16c_0.05n.data --labels $DATA_DIR/10000000p_8d_16c_0.05n.data_labels --log "$LOG_DIR/10000000p_8d_16c_0.05n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )) ; sleep 1

# sensitivity to dimensionality

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_2d_16c_0.05n.data --labels $DATA_DIR/10000000p_2d_16c_0.05n.data_labels --log "$LOG_DIR/10000000p_2d_16c_0.05n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )); sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_16d_16c_0.05n.data --labels $DATA_DIR/10000000p_16d_16c_0.05n.data_labels --log "$LOG_DIR/10000000p_16d_16c_0.05n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )) ; sleep 1

# sensitivity to # of clusters (K)

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_1c_0.05n.data --labels $DATA_DIR/10000000p_8d_1c_0.05n.data_labels --log "$LOG_DIR/10000000p_8d_1c_0.05n.${NUM_MACHINE}m.log" --k 1 --pars $(( NUM_MACHINE*16 )) ; sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_4c_0.05n.data --labels $DATA_DIR/10000000p_8d_4c_0.05n.data_labels --log "$LOG_DIR/10000000p_8d_4c_0.05n.${NUM_MACHINE}m.log" --k 4 --pars $(( NUM_MACHINE*16 )) ; sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_64c_0.05n.data --labels $DATA_DIR/10000000p_8d_64c_0.05n.data_labels --log "$LOG_DIR/10000000p_8d_64c_0.05n.${NUM_MACHINE}m.log" --k 64 --pars $(( NUM_MACHINE*16 )) ; sleep 1

# sensitivity to noise

spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_16c_0.0n.data --labels $DATA_DIR/10000000p_8d_16c_0.0n.data_labels --log "$LOG_DIR/10000000p_8d_16c_0.0n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )) ; sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_16c_0.01n.data --labels $DATA_DIR/10000000p_8d_16c_0.01n.data_labels --log "$LOG_DIR/10000000p_8d_16c_0.01n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )) ; sleep 1
spark-submit $SPARK_PARAMS $APP_JAR --data $DATA_DIR/10000000p_8d_16c_0.1n.data --labels $DATA_DIR/10000000p_8d_16c_0.1n.data_labels --log "$LOG_DIR/10000000p_8d_16c_0.1n.${NUM_MACHINE}m.log" --k 16 --pars $(( NUM_MACHINE*16 )) ; sleep 1
