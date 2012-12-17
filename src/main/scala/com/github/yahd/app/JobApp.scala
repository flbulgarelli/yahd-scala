package com.github.yahd.app

class JobApp(jobName: String) extends JobRunner {

  def main(args: Array[String]) = runJob(jobName, args)

}