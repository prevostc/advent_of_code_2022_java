package com.prevostc.utils;

// http://lambda-the-ultimate.org/node/2694
public abstract class Either<A, B> {
    private Either() {
    }

    public A left() {
        return null;
    }

    public B right() {
        return null;
    }

    public boolean isLeft() {
        return false;
    }

    public boolean isRight() {
        return false;
    }

    public static final class Left<A, B> extends Either<A, B> {
        private A a;

        public Left(A a) {
            this.a = a;
        }

        @Override
        public A left() {
            return a;
        }

        @Override
        public boolean isLeft() {
            return true;
        }
    }

    public static final class Right<A, B> extends Either<A, B> {
        private B b;

        public Right(B b) {
            this.b = b;
        }

        @Override
        public B right() {
            return b;
        }

        @Override
        public boolean isRight() {
            return true;
        }
    }

    public interface Map<A, B> {
        B apply(A a);
    }

    public <C> C either(Map<A, C> f, Map<B, C> g) {
        if (this instanceof Left) {
            return f.apply(left());
        }
        return g.apply(right());
    }
}
