package lectures.part3_concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration._

object FuturesAndPromises extends App {

  def calcMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calcMeaningOfLife
  }

  println(aFuture.value)

  println("waiting on future")
  aFuture.onComplete {
    case Success(value) => println(value)
    case Failure(exception) => println(exception)
  }

  Thread.sleep(2000)


  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.3-dummy" -> "Dummy"
    )
    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )
    val random = new Random()

    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(3000))
      Profile(id, names(id))
    }

    def fetchABestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }

  }

  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) =>
      val bill = SocialNetwork.fetchABestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(e) => e.printStackTrace()
      }
    case Failure(e) => e.printStackTrace()
  }

  Thread.sleep(2000)


  // functional composition of futures

  // map flatMap and filter
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriends = mark.flatMap(profile => SocialNetwork.fetchABestFriend(profile))
  val zucksBestFriends = marksBestFriends.filter(profile => profile.name.startsWith("Z"))

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchABestFriend(mark)
  } mark.poke(bill)

  // fallbacks
  SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.3-dummy", "dsf")
  }

  SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.3-dummy")
  }

  SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.3-dummy"))


  // blocking on futures
  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "the bank"

    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Success")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch user
      // create transaction user -> merchant
      // wait for transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("A", "iphone", "M", 3000))

  // promises
  val promise = Promise[Int]() // controller over a future
  val future = promise.future

  future.onComplete {
    case Success(r) => println("received " + r)
  }

  val producer = new Thread(() => {
    println("producing stuff")
    Thread.sleep(500)
    // fulfilling the promise
    promise.success(42) // manipulates the future to complete successfully
    println("producer done")
  })

  producer.start()
  Thread.sleep(1000)


  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  def inSequence[A, B](fa: Future[A], fb: Future[B]): Future[B] = {
    fa.flatMap(_ => fb)
  }

  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]
    // only the first one will succeed setting the value, return that one
    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)
    promise.future
  }

  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promiseBoth = Promise[A]
    val promiseLast = Promise[A]

    def checkAndCompleter(result: Try[A]) = {
      if (!promiseBoth.tryComplete(result)) { // the other already completed it
        promiseLast.complete(result)
      }
    }

    fa.onComplete(checkAndCompleter)
    fb.onComplete(checkAndCompleter)

    promiseLast.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }
  val slow = Future {
    Thread.sleep(200)
    43
  }
  first(fast, slow).foreach(println)
  last(fast, slow).foreach(println)
  Thread.sleep(1000)

  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
    action().filter(condition).recoverWith {
      case _ => retryUntil(action, condition)
    }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextVal = random.nextInt(100)
    println(nextVal)
    nextVal
  }
  retryUntil(action, (x: Int) => x < 50).foreach(println)

  Thread.sleep(5000)

}
