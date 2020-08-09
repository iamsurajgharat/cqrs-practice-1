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
  def processActivity(): ServiceCall[Activity, ActivityStatus]

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

case class Activity(
    id: Option[String],
    userId: String,
    user2Id: Option[String],
    activityType: String,
    feedId: Option[String],
    commentId: Option[String],
    content: Option[String]
)

object Activity {
  implicit val format: Format[Activity] = Json.format[Activity]
}

sealed trait ActivityStatus
case class ActivityStatusSucess(activity: Activity) extends ActivityStatus
case class ActivityStatusFailure(activity: Activity, error: String)
    extends ActivityStatus

object ActivityStatus {
  implicit val format: Format[ActivityStatus] =
    Json.format[ActivityStatus]
}

object ActivityStatusSucess {
  implicit val format: Format[ActivityStatusSucess] =
    Json.format[ActivityStatusSucess]
}

object ActivityStatusFailure {
  implicit val format: Format[ActivityStatusFailure] =
    Json.format[ActivityStatusFailure]
}
