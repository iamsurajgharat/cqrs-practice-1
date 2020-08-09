package com.surajgharat.practice.cqrs.activity.impl

import com.surajgharat.practice.cqrs.activity.api.ActivityService
import com.surajgharat.practice.cqrs.activity.api.Activity
import com.lightbend.lagom.scaladsl.api.ServiceCall
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class ActivityServiceImpl(implicit ec: ExecutionContext)
    extends ActivityService {
  override def processActivity(): ServiceCall[Activity, String] =
    ServiceCall { x =>
      Future {
        "Yes done\n"
      }
    }
}
