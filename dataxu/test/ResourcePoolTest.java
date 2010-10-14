import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class ResourcePoolTest {
    
    private ResourcePool<Integer> pool;
    
    private final Executor        executor = Executors.newSingleThreadExecutor();
    
    @Before
    public void setUp() throws Exception {
        pool = new ResourcePoolImpl<Integer>();
        pool.open();
    }
    
    @Test
    public void should_aquire_resource_from_pool() throws Exception {
        pool.add( 1 );
        assertThat( "Aquired a resource", pool.acquire(), is( 1 ) );
    }
    
    @Test
    public void should_aquire_with_timeout_resource_from_pool() throws Exception {
        pool.add( 1 );
        assertThat( "Aquired a resource", pool.acquire( 1, TimeUnit.SECONDS ), is( 1 ) );
    }
    
    @Test( timeout = 1000 )
    public void should_block_on_aquire_until_there_is_a_resource() throws Exception {
        executor.execute( new Runnable() {
            
            public void run() {
                try {
                    Thread.sleep( 500 );
                    pool.add( 1 );
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } );
        final long start = System.nanoTime();
        final Integer resource = pool.acquire();
        final long duration = System.nanoTime() - start;
        assertThat( "Aquired a resource", resource, is( 1 ) );
        assertThat( "Duration > 500ms", TimeUnit.NANOSECONDS.toMillis( duration ) >= 500, is( true ) );
    }
    
    @Test( timeout = 1000 )
    public void should_block_on_aquire_with_timeout_until_there_is_a_resource() throws Exception {
        executor.execute( new Runnable() {
            
            public void run() {
                try {
                    Thread.sleep( 500 );
                    pool.add( 1 );
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } );
        final long start = System.nanoTime();
        final Integer resource = pool.acquire( 1, TimeUnit.SECONDS );
        final long duration = System.nanoTime() - start;
        assertThat( "Aquired a resource", resource, is( 1 ) );
        assertThat( "Duration > 500ms", TimeUnit.NANOSECONDS.toMillis( duration ) >= 500, is( true ) );
    }
    
    @Test( timeout = 1000 )
    public void should_block_on_close_until_resource_is_released() throws Exception {
        pool.add( 1 );
        final Integer resource = pool.acquire();
        executor.execute( new Runnable() {
            
            public void run() {
                try {
                    Thread.sleep( 500 );
                    pool.release( resource );
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } );
        final long start = System.nanoTime();
        pool.close();
        final long duration = System.nanoTime() - start;
        assertThat( "Duration > 500ms", TimeUnit.NANOSECONDS.toMillis( duration ) >= 500, is( true ) );
    }
    
    @Test( timeout = 1000 )
    public void should_block_on_remove_until_resource_is_released() throws Exception {
        pool.add( 1 );
        final Integer resource = pool.acquire();
        executor.execute( new Runnable() {
            
            public void run() {
                try {
                    Thread.sleep( 500 );
                    pool.release( resource );
                }
                catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } );
        final long start = System.nanoTime();
        final boolean value = pool.remove( resource );
        final long duration = System.nanoTime() - start;
        assertThat( "Remove returned correct value", value, is( true ) );
        assertThat( "Duration > 500ms", TimeUnit.NANOSECONDS.toMillis( duration ) >= 500, is( true ) );
    }
    
    @Test
    public void should_release_resource() throws Exception {
        pool.add( 1 );
        pool.acquire();
        pool.release( 1 );
        assertThat( "Can aquire resource again", pool.acquire(), is( 1 ) );
    }
    
    @Test
    public void should_return_false_for_is_open_after_call_to_close() throws Exception {
        pool.close();
        assertThat( "Pool is closed", pool.isOpen(), is( false ) );
    }
    
    @Test
    public void should_return_false_for_is_open_after_call_to_close_now() throws Exception {
        pool.closeNow();
        assertThat( "Pool is closed", pool.isOpen(), is( false ) );
    }
    
    @Test
    public void should_return_false_for_remove_now_resource_that_was_not_added() throws Exception {
        assertThat( "Removing resource returns correct value", pool.removeNow( 1 ), is( false ) );
    }
    
    @Test
    public void should_return_false_for_remove_resource_that_was_not_added() throws Exception {
        assertThat( "Removing resource returns correct value", pool.remove( 1 ), is( false ) );
    }
    
    @Test
    public void should_return_true_for_is_open_after_call_to_open() throws Exception {
        pool.open();
        assertThat( "Pool is open", pool.isOpen(), is( true ) );
    }
    
    @Test
    public void should_return_true_for_remove_now_resource_that_was_added() throws Exception {
        pool.add( 1 );
        assertThat( "Removing resource returns correct value", pool.removeNow( 1 ), is( true ) );
    }
    
    @Test
    public void should_return_true_for_remove_resource_that_was_added() throws Exception {
        pool.add( 1 );
        assertThat( "Removing resource returns correct value", pool.remove( 1 ), is( true ) );
    }
    
    @Test( expected = IllegalStateException.class )
    public void should_throw_exception_trying_to_aquire_from_closed_pool() throws Exception {
        pool.close();
        pool.acquire();
    }
    
    @Test( expected = IllegalStateException.class )
    public void should_throw_exception_trying_to_aquire_with_timeout_from_closed_pool() throws Exception {
        pool.close();
        pool.acquire( 1, TimeUnit.SECONDS );
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void should_throw_exception_trying_to_release_resource_that_was_not_aquired() throws Exception {
        pool.release( 1 );
    }
    
    @Test( timeout = 1000 )
    public void should_timeout_aquiring_resource_from_empty_pool() throws Exception {
        final Integer resource = pool.acquire( 100, TimeUnit.MILLISECONDS );
        assertThat( "Aquired a resource", resource, is( nullValue() ) );
    }
}
