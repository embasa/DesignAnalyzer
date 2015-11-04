package demo;


class A {
    public void meth1() {
        System.out.println("calling " + getClass() + ".meth1");
    }

    public void meth2() {
        System.out.println("calling " + getClass() + ".meth2");
    }
}

class B extends A {
    public void meth1() {
        System.out.println("calling " + getClass() + ".meth1");
    }

    public void meth2() {
        System.out.println("calling " + getClass() + ".meth2");
    }
}

class C extends B {
    public void meth1() {
        System.out.println("calling " + getClass() + ".meth1");
    }

    public void meth2() {
        System.out.println("calling " + getClass() + ".meth2");
    }

    public void meth3() {
        System.out.println("calling " + getClass() + ".meth3");
    }

    public void meth4() {
        System.out.println("calling " + getClass() + ".meth4");
    }
}

class D {
    private B b = new B();
    public void meth1() {
        System.out.println("calling " + getClass() + ".meth1");
    }
}

class E {
    public void meth1(D d, A a) {
        System.out.println("calling " + getClass() + ".meth1");
    }

    public void meth2() {
        System.out.println("calling " + getClass() + ".meth2");
    }
}

class F {
    private D d = new D();
    public void meth1(E e) {
        System.out.println("calling " + getClass() + ".meth1");
    }

    public void meth2() {
        System.out.println("calling " + getClass() + ".meth2");
    }
}


public class Demo {

    public static void main(String[] args) {
        A a = new A();
        a.meth1();
        a.meth2();
        B b = new B();
        b.meth1();
        b.meth2();
        // etc.
    }
}

