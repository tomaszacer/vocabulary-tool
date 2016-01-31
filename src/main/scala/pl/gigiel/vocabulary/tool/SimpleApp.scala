package pl.gigiel.vocabulary.tool

import java.sql.DriverManager
import java.sql.Connection
import org.h2.tools.Server

object SimpleApp extends App {
  //val fileName = "E:\\_tgl\\workspace-tomaszacer\\vocabulary-tool-confidential\\resources\\jack-london-white-fang.txt"
  //val all = words(fileName)
  //println(all.length)
  //println(all)

  db
  println("ok")

  def words(fileName: String): List[String] = {
    val source = scala.io.Source.fromFile(fileName)
    val wordPattern = """([A-Za-z\-'])+""".r
    val words = try source.getLines.flatMap(wordPattern.findAllIn).map(_.toLowerCase).toList.distinct.sorted finally source.close()

    return words
  }

  def db: Unit = {
    val driver = "org.h2.Driver"
    val url = "jdbc:h2:mem:vocabulary-db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false"
    val username = "vocabulary-username"
    val password = "vocabulary-password"

    var server: Server = null
    var connection: Connection = null

    try {
      Class.forName(driver) // unnecessary in jdbc 4+
      server = Server.createTcpServer("-tcpPort", "9123", "-tcpAllowOthers").start
      // jdbc:h2:tcp://localhost:9123/mem:vocabulary-db;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false
      connection = DriverManager.getConnection(url, username, password)

      val stmt = connection.createStatement()
      stmt.execute("create table infos(id int primary key, info varchar(255))")
      stmt.execute("insert into infos(id, info) values(1, 'abc')")
      val rs = stmt.executeQuery("select id, info from infos")
      while (rs.next()) {
        val id = rs.getString("id")
        val info = rs.getString("info")
        println("id, info = " + id + ", " + info)
      }
      io.StdIn.readInt
    } catch {
      case e: Throwable => e.printStackTrace
    } finally {
      connection.close
      server.stop
    }
  }
}
