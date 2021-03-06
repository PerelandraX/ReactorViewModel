package com.perelandrax.sample.githubsearch.rxevent

import com.jakewharton.rxrelay3.Relay
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable

/**
 *
 */
object RxEvent {

  private val _publisher = EventPublisher.create<Event>().toSerialized()

  val publisher: Relay<Event> get() = _publisher

  fun publish(event: Event) {
    return publisher.accept(event)
  }

  fun <T> observe(eventType: Class<T>): Flowable<T> {
    return publisher.ofType(eventType).toFlowable(BackpressureStrategy.BUFFER)
  }

  // Using ofType operator to filter only match events class type
  inline fun <reified T : Event> observe(): Flowable<T> {
    return publisher.ofType(T::class.java).toFlowable(BackpressureStrategy.BUFFER)
  }
}