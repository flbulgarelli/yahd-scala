package com.github.yahd.app.registry
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import com.github.yahd._

import Yahd._
import Prelude._
import scala.collection.mutable.HashMap

class AppMapper[WA, WB, WC] extends Mapper[WLong, WA, WB, WC] {
  override def run(context: Mapper[WLong, WA, WB, WC]#Context) {
    MappersRegistry.mapperFor[WA, WB, WC](this).run(context)
  }
}

class AppMapper0 extends AppMapper
class AppMapper1 extends AppMapper
class AppMapper2 extends AppMapper
class AppMapper3 extends AppMapper
class AppMapper4 extends AppMapper

object MappersRegistry extends YahdRegistry[Mapper[_, _, _, _]]("AppMapper", "mapper") {

  populatePool(4)

  def mapperFor[WA, WB, WC](appMapper: AppMapper[_, _, _]) =
    elementsForClasses(appMapper.getClass).asInstanceOf[Mapper[WLong, WA, WB, WC]]

  def registerMapper[WA, WB, WC](mapper: Mapper[WLong, WA, WB, WC]) = register(mapper)
}

class AppReducer[WA, WB, WC, WD] extends Reducer[WA, WB, WC, WD] {
  override def run(context: Reducer[WA, WB, WC, WD]#Context) {
    ReducersRegistry.reducerFor[WA, WB, WC, WD](this).run(context)
  }
}

class AppReducer0 extends AppReducer
class AppReducer1 extends AppReducer
class AppReducer2 extends AppReducer
class AppReducer3 extends AppReducer
class AppReducer4 extends AppReducer
class AppReducer5 extends AppReducer
class AppReducer6 extends AppReducer
class AppReducer7 extends AppReducer
class AppReducer8 extends AppReducer
class AppReducer9 extends AppReducer


object ReducersRegistry extends YahdRegistry[Reducer[_, _, _, _]]("AppReducer", "reducer") {

  populatePool(9)

  def reducerFor[WA, WB, WC, WD](appReducer: AppReducer[_, _, _, _]) =
    elementsForClasses(appReducer.getClass).asInstanceOf[Reducer[WA, WB, WC, WD]]

  def registerReducer[WA, WB, WC, WD](reducer: Reducer[WA, WB, WC, WD]) = register(reducer)

}

class YahdRegistry[A](baseClassName: String, elementName: String) {
  var classesPool = new Pool[Class[_ <: A]]
  var elementsForClasses = new HashMap[Class[_], A]

  protected def populatePool(count: Int) =
    (0 to count).foreach { n =>
      classesPool += Class.forName("com.github.yahd.app.registry." + baseClassName + +n).asInstanceOf[Class[_ <: A]]
    }

  protected def register(element: A) =
    classesPool.acquire.map { it =>
      elementsForClasses.put(it, element)
      it
    }.getOrElse {
      throw new UnsupportedOperationException("Too much " + elementName + "s required. Yahd currently supports up to " + classesPool.size)
    }
}