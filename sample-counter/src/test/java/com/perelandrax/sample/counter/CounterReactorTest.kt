package com.perelandrax.sample.counter

import com.perelandrax.sample.counter.ui.main.CounterReactor
import com.perelandrax.sample.counter.ui.main.CounterReactor.Action.Decrease
import com.perelandrax.sample.counter.ui.main.CounterReactor.Action.Increase
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.test.assertEquals

/**
 * Reactor 테스트
 * (리액터는 뷰에 비해서 상대적으로 테스트하기 쉽습니다. Action이 전달되었을 때 비즈니스 로직을 수행하여 State가 바뀌는지를 확인하면 됩니다.)
 * - Action을 받았을때 원하는 State로 잘 변경되는지
 *
 * @property schedulersRule SchedulersRule Rx 테스트를 위해 Rule를 설정한다.
 */
class CounterReactorTest {

  @get:Rule
  val schedulersRule = SchedulersRule()

  private lateinit var reactor: CounterReactor

  @Before
  fun setUp() {
    // 리액터를 초기값(State count 0)으로 생성한다.
    reactor = CounterReactor()
    reactor.initialState = CounterReactor.State(count = 0)
  }

  @After
  fun tearDown() {

  }

  /**
   * Increase 액션이 발생할 경우 값을 1 증가시킨다.
   */
  @Test
  fun testState_givenStateValue0_whenActionIncrease_thenShouldStateValuePlus1() {
    // 리액터에 Action Increase를 전달한다.
    reactor.action.accept(Increase).apply { Thread.sleep(500) }

    // 리액터 State의 count 값이 1이 되었는지 검증한다.
    assertEquals(reactor.currentState.count, 1)
  }

  /**
   * Decrease 액션이 발생할 경우 값을 1 감소시킨다.
   */
  @Test
  fun testState_givenStateValue0_whenActionDecrease_thenShouldStateValueMinus1() {
    // 리액터에 Action Decrease를 전달한다.
    reactor.action.accept(Decrease).apply { Thread.sleep(500) }

    // 리액터 State의 count 값이 -1이 되었는지 검증한다.
    assertEquals(reactor.currentState.count, -1)
  }

  /**
   * Rx 테스트를 위해 스케줄러를 세팅하는 클래스
   */
  class SchedulersRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
      return object : Statement() {

        @Throws(Throwable::class)
        override fun evaluate() {
          RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
          RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
          RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
          RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }
          RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

          try {
            base.evaluate()
          } finally {
            RxJavaPlugins.reset()
            RxAndroidPlugins.reset()
          }
        }
      }
    }
  }
}