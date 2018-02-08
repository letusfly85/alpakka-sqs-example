package io.wonder.soft.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.scaladsl.SqsSource
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()

    implicit val sqsClient =
      AmazonSQSAsyncClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).build()

    implicit val system = ActorSystem()
    implicit val mat = ActorMaterializer()

    val sqsSourceURL = config.getString("aws.sqs.url")

    SqsSource(sqsSourceURL)
      .runForeach((message) => {
        println(message.getBody)
        sqsClient.deleteMessage(
          new DeleteMessageRequest(sqsSourceURL, message.getReceiptHandle)
        )
      })

  }
}
