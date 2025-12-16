package com.footballsync

import cats.effect._
import cats.effect.std.Random
import cats.syntax.all._

import scala.concurrent.duration._

package object util {

  def randomDelay[F[_]: Async](baseDelay: FiniteDuration, randomRange: FiniteDuration): F[FiniteDuration] = {
    Random.scalaUtilRandom[F].flatMap { random =>
      random
        .nextIntBounded(randomRange.toMillis.toInt)
        .map { randomExtra =>
          baseDelay + randomExtra.millis
        }
    }
  }
}
