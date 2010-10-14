import java.util.concurrent.TimeUnit;

public interface ResourcePool<R> {
    
    R acquire() throws InterruptedException;
    
    R acquire( long timeout, TimeUnit timeUnit ) throws InterruptedException;
    
    boolean add( R resource );
    
    void close() throws InterruptedException;
    
    void closeNow();
    
    boolean isOpen();
    
    void open();
    
    void release( R resource );
    
    boolean remove( R resource ) throws InterruptedException;
    
    boolean removeNow( R resource );
}
