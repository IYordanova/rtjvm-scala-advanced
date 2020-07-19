package lectures.part3_concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  // 1 - parallel collections -  overhead
  val parList = List(1, 2, 3).par

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    list.par.map(_ + 1)
  }
  println("serial time " + serialTime)
  println("parallel time " + parallelTime)


  /*  Map-reduce model
    - split the elements into chunks - splitter
    - operation
    - recombine - combiner
  */
  println(List(1, 2, 3).reduce(_ - _))
  println(List(1, 2, 3).par.reduce(_ - _)) // not in order !!!


  /*
     synchronization required on other obj used
   */
  var sum = 0
  List(1, 2, 3).par.foreach(sum += _)
  println(sum) // race conditions - sum not guaranteed

  /*
     configuring parallel collections - can use also:
     - ExecutionContextTaskSupport(ExecutionContext) // compatible with futures
     - implement the TaskSupport trait entirely
   */
  val parVector = new ParVector[Int]
  parVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))


  // 2 - Atomic ops and references
  val atomic = new AtomicReference[Int](2)
  val currentVal = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write
  atomic.getAndSet(5) // thread-safe combo
  atomic.compareAndSet(38, 56) // thread-safe - if value == 38, then set it to 56, shallow comparison
  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1)
  atomic.accumulateAndGet(12, _ + _) //  take 12, get the val, add, set and return all in once
  atomic.getAndAccumulate(12, _ + _)

}
