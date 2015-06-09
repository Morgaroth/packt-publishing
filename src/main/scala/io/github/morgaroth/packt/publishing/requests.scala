package io.github.morgaroth.packt.publishing

import spray.client.pipelining._

object SiteIndex {
  def request = Get("https://www.packtpub.com/")
}

object LoginPost{
  def request = Post("https://www.packtpub.com/")
}
