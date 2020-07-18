package lectures.part3_concurrency

import java.util.concurrent.Executors

object Intro extends App {

    val aThread = new Thread(() => println("hey"))
    aThread.start()
    aThread.join()

    val anotherThread = new Thread(() => (1 to 5).foreach(_ => println("a")))
    val thirdThread = new Thread(() => (1 to 5).foreach(_ => println("b")))

    val pool = Executors.newFixedThreadPool(10)
    pool.execute(() => println("sjhb"))
    pool.execute(() => {
      Thread.sleep(1000)
      println("aaa")
    })
    pool.execute(() => {
      Thread.sleep(2000)
      println("b")
      Thread.sleep(2000)
      println("c")
    })
    pool.shutdown()

    def runInParallel = {
      var x = 0
      val thread1 = new Thread(() => {
        x = 1
      })

      val thread2 = new Thread(() => {
        x = 2
      })

      thread1.start()
      thread2.start()
      println(x)
    }

    // race condition
    for(_ <- 1 to 10000) runInParallel  // mostly 0, once 1


  class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
  }

  for (_ <- 1 to 10000) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000))
    val thread2 = new Thread(() => buy(account, "iphone", 4000))
    thread1.start()
    thread2.start()
    Thread.sleep(10)
    if (account.amount != 43000) println("AHA " + account.amount)
  }

  // 1. use synchronize
  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized {
      // no 2 threads can eval this at the same time
      account.amount -= price
    }
  }

  // 2. use volatile
  class BankAccountSafe(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }


  /*
     print in reverse
   */
  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThreads) {
      val newThread = inceptionThreads(maxThreads, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"blah blah thread $i")
  })

  inceptionThreads(20).start()


  /*
      - biggest value possible for x - if sequentially -> 100
      - smallest value possible for x - if all run together, all read 0 and increase to 1 -> 1
   */
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())


  /*
     - the value for message at the end
   */
  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "sfldnf"
  })

  message = "sdf"
  awesomeThread.start()
  Thread.sleep(2000)


}
