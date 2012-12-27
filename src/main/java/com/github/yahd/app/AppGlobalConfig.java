package com.github.yahd.app;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

@SuppressWarnings("rawtypes")
public class AppGlobalConfig {

  private static Mapper mapper;
  private static Reducer reducer;
  private static Reducer combiner;

  public static Reducer getCombiner() {
    return combiner;
  }

  public static Reducer getReducer() {
    return reducer;
  }

  public static Mapper getMapper() {
    return mapper;
  }

  public static <WA, WB, WC, WD, WE> void init(
    Mapper<LongWritable, WA, WB, WC> mapper, 
    Reducer<WB, WC, WB, WC> combiner, Reducer<WB, WC, WD, WE> reducer) {
    AppGlobalConfig.mapper = mapper;
    AppGlobalConfig.reducer = reducer;
    AppGlobalConfig.combiner = combiner;
  }

  public static void configureJob(Job job) {
    job.setMapperClass(AppMapper.class);
    if (reducer != null) job.setReducerClass(AppReducer.class);
    if (combiner != null) job.setCombinerClass(AppCombiner.class);
  }
}
