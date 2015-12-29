package net.bluepoet.mockobject.sample

import net.bluepoet.mockobject.HelloSpock
import spock.lang.Specification

/**
 * Created by poet.me on 15. 12. 29..
 */
class HelloSpockTest extends Specification {
    def "SayHello"() {
        given:
        def helloSpock = new HelloSpock();
        when:
        def expected = helloSpock.sayHello("bluepoet");
        then:
        expected == "hello bluepoet"
    }
}
