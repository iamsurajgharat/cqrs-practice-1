package com.surajgharat.practice.cqrs.activity.api

import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

/**
  * The Activity service interface
  */
trait ActivityService extends Service {

  /**
    * Only one action and that is to handle upcoming event
    *
    * @return status of the event whether it got processed successfully or not
    */
  def processActivity(): ServiceCall[Activity, String]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("activity")
      .withCalls(
        pathCall("/api/activity", processActivity _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}

case class Activity(userId: String)

object Activity {
  implicit val format: Format[Activity] = Json.format[Activity]
}
