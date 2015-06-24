package io.github.morgaroth.packt.publishing

case class LoginData(
                      formId: FormId,
                      email: String,
                      password: String
                      )

case class UserInfo(
                     sessionToken: String
                     )