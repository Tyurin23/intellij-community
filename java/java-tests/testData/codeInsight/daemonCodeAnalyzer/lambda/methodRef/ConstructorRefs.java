class MyTest {
    MyTest() {
    }

    interface I {
        MyTest m();
    }

    static void test(I s) {
        s.m();
    }

    public static void main(String[] args) {
        I s = MyTest::new;
        s.m();
        test(MyTest::new);
    }
}

class MyTest1 {

    MyTest1(Object o) {
    }

    MyTest1(Number n) {
    }

    interface I {
        MyTest1 m(Object o);
    }

    static void test(I s, Object arg) {
        s.m(arg);
    }

    public static void main(String[] args) {
        I s = MyTest1::new;
        s.m("");
        test(MyTest1::new, "");
    }
}

class MyTest2<X> {
    MyTest2(X x) {
    }

    interface I<Z> {
        MyTest2<Z> m(Z z);
    }

    static <Y> void test(I<Y> s, Y arg) {
        s.m(arg);
    }

    public static void main(String[] args) {
        I<String> s = MyTest2<String>::new;
        s.m("");
        test(MyTest2<String>::new, "");
    }
}

class MyTest3<X> {

    MyTest3(X x) { }

    interface I<Z> {
        MyTest3<Z> m(Z z);
    }

    static void test(I<Integer> s) {   }

    public static void main(String[] args) {
        <error descr="Incompatible types. Found: '<method reference>', required: 'MyTest3.I<java.lang.Integer>'">I<Integer> s = MyTest3<String>::new;</error>
        test<error descr="'test(MyTest3.I<java.lang.Integer>)' in 'MyTest3' cannot be applied to '(<method reference>)'">(MyTest3<String>::new)</error>;
    }
}

class MyTestInvalidQ {
  class Super {}
  
  class ConstructorRefs extends Super {
      void test() {
          ConstructorRefs refs = new ConstructorRefs();
          BlahBlah b0 = <error descr="Cannot find class refs">refs</error>::new;
          BlahBlah blahBlah = <error descr="Cannot find class this">this</error>::new;
          BlahBlah1 blahBlah1 = <error descr="Cannot find class super">super</error>::new;
      }
  }
  
  interface BlahBlah {
      ConstructorRefs foo();
  }

  interface BlahBlah1 {
      Super foo();
  }

  abstract static class A {
    interface I {
      A foo();
    }
    <error descr="Incompatible types. Found: '<method reference>', required: 'MyTestInvalidQ.A.I'">I i = A :: new;</error>
  }
}