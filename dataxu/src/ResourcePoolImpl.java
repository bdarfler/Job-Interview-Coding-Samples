import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ResourcePoolImpl<R> implements ResourcePool<R> {
    
    private static final String                    CLOSED_POOL_EXCEPTION = "Pool is closed, cannot aquire resource.";
    
    private static final String                    RELEASE_EXCEPTION     = "Unaquired resource, cannot release it.";
    
    private volatile boolean                       open                  = false;
    
    private final BlockingQueue<R>                 available             = new LinkedBlockingQueue<R>();
    
    private final ConcurrentMap<R, CountDownLatch> aquired               = new ConcurrentHashMap<R, CountDownLatch>();
    
    public R acquire() throws InterruptedException {
        if ( !open ) { throw new IllegalStateException( CLOSED_POOL_EXCEPTION ); }
        final R resource = available.take();
        if ( resource != null ) {
            aquired.put( resource, new CountDownLatch( 1 ) );
        }
        return resource;
    }
    
    public R acquire( final long timeout, final TimeUnit timeUnit ) throws InterruptedException {
        if ( !open ) { throw new IllegalStateException( CLOSED_POOL_EXCEPTION ); }
        final R resource = available.poll( timeout, timeUnit );
        if ( resource != null ) {
            aquired.put( resource, new CountDownLatch( 1 ) );
        }
        return resource;
    }
    
    public boolean add( final R resource ) {
        return available.add( resource );
    }
    
    public void close() throws InterruptedException {
        open = false;
        for ( final CountDownLatch latch : aquired.values() ) {
            latch.await();
        }
    }
    
    public void closeNow() {
        open = false;
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public void open() {
        open = true;
    }
    
    public void release( final R resource ) {
        final CountDownLatch latch = aquired.get( resource );
        if ( latch == null ) { throw new IllegalArgumentException( RELEASE_EXCEPTION ); }
        available.add( resource );
        latch.countDown();
    }
    
    public boolean remove( final R resource ) throws InterruptedException {
        final CountDownLatch latch = aquired.get( resource );
        if ( latch != null ) {
            latch.await();
        }
        return available.remove( resource );
    }
    
    public boolean removeNow( final R resource ) {
        return available.remove( resource );
    }
}
