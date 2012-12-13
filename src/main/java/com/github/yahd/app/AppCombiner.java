package com.github.yahd.app;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AppCombiner extends Reducer {

  public void run(Context context) throws IOException, InterruptedException {
    App.getCombiner().run(context);
  }

}
