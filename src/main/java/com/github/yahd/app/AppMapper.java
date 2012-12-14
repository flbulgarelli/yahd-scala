package com.github.yahd.app;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Mapper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AppMapper extends Mapper {
	public void run(Context context) throws IOException, InterruptedException {
		AppGlobalConfig.getMapper().run(context);
	}

}
