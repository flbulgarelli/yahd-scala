package com.github.yahd.app

/**
 * A JobRunner that can be executed as a main class.
 *
 * Yahd Jobs should be defined by inheriting this class.
 *
 * ''Please notice that currently no more than a single
 *  Job can be configured per application''
 *
 * @param jobName the name of the Yahd Job, required by Hadopp
 * @see org.apache.hadoop.mapreduce.Job
 *
 * @author flbulgarelli
 */
class JobApp(jobName: String) extends JobRunner {

  def main(args: Array[String]) = runJob(jobName, args)
}