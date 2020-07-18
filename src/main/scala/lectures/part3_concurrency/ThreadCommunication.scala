package lectures.part3_concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def get = {
      val result = value
      value = 0
      result
    }

    def set(newValue: Int) = value = newValue
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("consumer waiting..")
      while (container.isEmpty) {
        println("consumer waiting loop")
      }
      println("consumer consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("producer computing..")
      Thread.sleep(500)
      val value = 42
      println("producer produced " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //  naiveProdCons()


  def smarterProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("consumer waiting..")
      container.synchronized {
        container.wait() // releases the lock on the container and waits at this point for "notify"
      }
      println("consumer consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("producer computing..")
      Thread.sleep(500)
      val value = 42
      container.synchronized {
        println("producer produced " + value)
        container.set(value)
        container.notify() // wakes up the consumer
      }
    })

    consumer.start()
    producer.start()
  }

  //  smarterProdCons()


  def prodConsLargerBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("consumer waiting - buffer empty")
            buffer.wait()
          }
          // at least one item in the buffer
          val x = buffer.dequeue()
          println("consumer consumed " + x)
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("producer - buffer is full")
            buffer.wait()
          }
          // at least one empty space in the buffer
          buffer.enqueue(i)
          println("producer produced " + i)
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  //  prodConsLargerBuffer()


  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"consumer $id waiting - buffer empty")
            buffer.wait()
          }
          // at least one item in the buffer
          val x = buffer.dequeue()
          println(s"consumer $id consumed $x")
          buffer.notify() // notifies ONE other thread
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0
      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"producer $id - buffer is full")
            buffer.wait()
          }
          // at least one empty space in the buffer
          buffer.enqueue(i)
          println(s"producer $id produced $i")
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3
    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }


  //  multiProdCons(3, 3)


  def testNotifyAll(): Unit = {
    val bell = new Object
    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"thread $i waiting")
        bell.wait()
        println(s"thread $i woken up")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("WAKE UP!!!")
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()
  }

  //  testNotifyAll()


  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized{
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"

    def switchSide(): Unit = {
      if(side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while(this.side == other.side) {
        println(s"$this: oh but please feel free to pass, $other")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("sam")
  val pier = Friend("pier")

  // !!! deadlock !!!
//  new Thread(() => sam.bow(pier)).start() // sam's lock -> pier's lock
//  new Thread(() => pier.bow(sam)).start()  // pier's lock -> sam's lock

  // !!! livelock !!! - threads are not blocked but none actually does anything
  new Thread(() => sam.pass(pier)).start()
  new Thread(() => pier.pass(sam)).start()
}
