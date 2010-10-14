Design Decisions

    The pool is implemented on top of a BlockinQueue. This choice was made to easily implement the blocking semantics of 
    the aquire() method calls.
    
    A volatile boolean was used as a status flag since this is the recommended use of volatile even though it has gotten a bad rap.
    
    CountDownLatches are used to signal when a resource is released. Its generally considered a better practice than using wait/notify
    since wait/notify can lead to missed signals.

    A ResourcePool interface was used to easily ensure the implementation conformed to the api specified in the problem writeup.

Thread Safety

    Thread safety is delegated to the BlockingQueue. It ensures that only one thread can obtain a given object.

Improvements

    1) While the remove() method adheres to the spec (i.e. it blocks until the resource is released() and it returns true if the pool is modified)
    it is possible that a resource could be released() and then aquired() before it can be removed() which would result in the remove() method return false. 
    This seems undesirable. Unfortunately, a stricter spec (i.e. ensuring that the resource is removed once it is released) would require additional locking which might undesirable as well.
    
    2) Adding proxies around the resources would be a nice next step. The benefit of this would be to prevent thread from calling methods on a resource after it was released.
    However, the use of Dynamic Proxies seemed outside the scope of this exercise.
    
Testing

    A unit test suite is included.