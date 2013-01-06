package com.github.yahd.app.pool
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter

import com.github.yahd.app.registry.Pool;

@RunWith(classOf[JUnitRunner])
class PoolTest extends FunSuite with BeforeAndAfter {

  var pool: Pool[String] = _
  
  before {
    pool = new Pool
    pool += "hello"
    pool += "foo"
    pool += "bar"
  }

  test("can acquire a single element when pool is completly free") {
    assert(pool.acquire === Some("hello"))
  }

  test("can acquire a single element when the pool capacit limit has not been reached") {
    pool.acquire
    assert(pool.acquire === Some("foo"))
  }

  test("can acquire a single element exectly before the limit has been reached") {
    pool.acquire
    pool.acquire
    assert(pool.acquire === Some("bar"))
  }

  test("can not acquire elements when the pool capacity limit has been reached") {
    pool.acquire
    pool.acquire
    pool.acquire
    assert(pool.acquire === None)
  }

}