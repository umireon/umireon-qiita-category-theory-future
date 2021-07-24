import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main {
  def theFutureList(): Seq[Future[Int]] = {
    1 to 10 map { Future(_) }
  }

  def theFuture1(value: Int, uuid: UUID): Future[(Int, String)] = {
    Future { (value * value, uuid.toString) }
  }

  def theFuture2(value: Int, str: String): Future[(Int, Char)] = {
    Future { (value - 10, str.head) }
  }

  def calculate1(): Future[Seq[(Int, Char)]] = {
    Future.sequence(theFutureList()).flatMap { list =>
      val uuid = UUID.randomUUID()
      Future.sequence(
        list.map { theFuture1(_, uuid) }
      ).flatMap { list2 =>
        Future.sequence(list2.map { case (value: Int, str: String) =>
          theFuture2(value, str)
        })
      }
    }
  }

  def calculate2(): Future[Seq[(Int, Char)]] = {
    Future sequence theFutureList map {
      (_, UUID.randomUUID())
    } flatMap { case (list: Seq[Int], uuid: UUID) =>
      Future sequence list.map {
        theFuture1(_, uuid)
      }
    } flatMap {
      Future sequence _.map { case (value: Int, str: String) =>
        theFuture2(value, str)
      }
    }
  }

  def calculate3(): Future[Seq[(Int, Char)]] = {
    Future sequence theFutureList zip Future(UUID.randomUUID()) flatMap {
      case (list: Seq[Int], uuid: UUID) =>
      Future.traverse(list) {
        theFuture1(_, uuid)
      }
    } flatMap {
      Future.traverse(_) { case (value: Int, str: String) =>
        theFuture2(value, str)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    calculate1().map { println }
    calculate2().map { println }
    calculate3().map { println }
  }
}
