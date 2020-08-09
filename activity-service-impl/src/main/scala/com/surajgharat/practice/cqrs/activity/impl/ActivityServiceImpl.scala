package com.surajgharat.practice.cqrs.activity.impl

import com.surajgharat.practice.cqrs.activity.api.ActivityService
import com.surajgharat.practice.cqrs.activity.api.Activity
import com.lightbend.lagom.scaladsl.api.ServiceCall
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.surajgharat.practice.cqrs.activity.api.ActivityStatusSucess
import com.surajgharat.practice.cqrs.activity.api.ActivityStatus
import com.surajgharat.practice.cqrs.activity.api.ActivityStatusSucess
import com.surajgharat.practice.cqrs.activity.api.ActivityStatusFailure
import com.lightbend.lagom.scaladsl.api.transport.PolicyViolation
import com.lightbend.lagom.scaladsl.api.transport.TransportErrorCode
import com.lightbend.lagom.scaladsl.api.transport.ExceptionMessage

class ActivityServiceImpl(implicit ec: ExecutionContext)
    extends ActivityService {
  override def processActivity(): ServiceCall[Activity, ActivityStatus] =
    ServiceCall { x =>
      val valResult = validate(x)
      Future {
        valResult match {
          case Some(value) =>
            throw new PolicyViolation(
              TransportErrorCode.BadRequest,
              new ExceptionMessage("Validation Failed", value.error)
            )
          case None =>
            val id = uuid
            val updatedActibity = x.activityType match {
              case "Follow" | "Unfollow" =>
                Activity(
                  id,
                  x.userId,
                  x.user2Id,
                  x.activityType,
                  None,
                  None,
                  None
                )
              case "Feed" =>
                val feedId = uuid
                Activity(
                  id,
                  x.userId,
                  None,
                  x.activityType,
                  feedId,
                  None,
                  x.content
                )
              case "Comment" =>
                val commentId = uuid
                Activity(
                  id,
                  x.userId,
                  None,
                  x.activityType,
                  x.feedId,
                  commentId,
                  x.content
                )
              case "Like" =>
                Activity(
                  id,
                  x.userId,
                  None,
                  x.activityType,
                  x.feedId,
                  x.commentId,
                  None
                )
            }
            ActivityStatusSucess(updatedActibity)
        }
      }

    }

  def validate(a: Activity): Option[ActivityStatusFailure] = {
    if (a == null || isBlank(a.userId) || isBlank(a.activityType))
      Some(ActivityStatusFailure(a, "Missing required data"))
    else {
      a.activityType match {
        case "Follow" | "Unfollow" if isBlank(a.user2Id) =>
          Some(ActivityStatusFailure(a, "User2Id is missing"))
        case "Comment" if isBlank(a.content) =>
          Some(ActivityStatusFailure(a, "comment content is missing"))
        case _ =>
          None
      }
    }
  }

  private def isBlank(str: String): Boolean = str == null || str.trim == ""
  private def isBlank(arg: Option[String]): Boolean =
    arg match {
      case None        => true
      case Some(value) => isBlank(value)
    }
  private def uuid: String = java.util.UUID.randomUUID().toString()
  implicit private def stringToSome(str: String): Option[String] =
    if (isBlank(str)) None else Some(str)
}
