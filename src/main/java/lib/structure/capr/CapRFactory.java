/**
 * 
 */
package lib.structure.capr;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.itextpdf.text.log.SysoCounter;

import lib.parser.aptaplex.AptaPlexConsumer;
import lib.parser.aptaplex.AptaPlexProducer;
import utilities.Configuration;

/**
 * @author Jan Hoinka 
 * Java implementation of CapR as described. This class controls the producer-consumer pattern
 */
public class CapRFactory implements Runnable{

	/**
	 * The progress of the parser instance. Writable to the consumers and thread-safe
	 */
	private static final AtomicInteger progress = new AtomicInteger();
	
	private Iterable<Entry<byte[], Integer>> items = null;
	
	public CapRFactory(Iterable<Entry<byte[], Integer>> items){
		
		this.items = items;
		
	}
	
	public void predict() {

//		// Creating shared object
//		BlockingQueue<Object> sharedQueue = new ArrayBlockingQueue<>(500); //TODO: MAKE THIS A PARAMETER
//
//		// We need to know how many threads we can use on the system
//		int num_threads = Math.min( Runtime.getRuntime().availableProcessors(), Configuration.getParameters().getInt("Performance.maxNumberOfCores"));
//		
//		// Creating Producer and Consumer Thread
//		Thread prodThread  = new Thread(new CapRFactoryProducer(items, sharedQueue), "CapR Producer");
//		
//		ArrayList<Thread> consumers = new ArrayList<Thread>();
//		
//		// We need at least one consumer
//		consumers.add(new Thread(new CapRFactoryConsumer(sharedQueue, progress), "CapR Consumer 1"));
//		
//		// Add remaining consumers
//		for (int x=1; x<num_threads-1; x++){
//			consumers.add(new Thread(new CapRFactoryConsumer(sharedQueue, progress), ("CapR Consumer " + (x+1)) ));
//		}
//
//		// Start the producer and consumer threads
//		for (int x=0; x<consumers.size(); x++){
//			consumers.get(x).start();
//		}
//		prodThread.start();
//
//		// Make sure the threads wait until completion
//		try {
//			for (int x=0; x<consumers.size(); x++){
//				consumers.get(x).join();
//			}
//			prodThread.join();
//		} catch (InterruptedException e) {
//			
//			// force poison pill in consumer thread
//			prodThread.interrupt();
//			
//			// make sure the consumers finish
//			for (int x=0; x<consumers.size(); x++){
//				consumers.get(x).interrupt();
//			}
//			
//			// propagate just in case
//			Thread.currentThread().interrupt();
//			
//		} finally {
//
//			// Clear resources used by the threads
//			for (int x=0; x<consumers.size(); x++){
//				consumers.set(x, null);
//			}
//			prodThread  = null;
//			
//		}
		
		
		// Creating shared object
		BlockingQueue<Object> sharedQueue = new ArrayBlockingQueue<>(500); 

		// We need to know how many threads we can use on the system
		int num_threads = Math.min( Runtime.getRuntime().availableProcessors(), Configuration.getParameters().getInt("Performance.maxNumberOfCores"));
		
		// Creating Producer and Consumer Threads using the ExecutorService to manage them
		ExecutorService es = Executors.newCachedThreadPool();
		es.execute(new CapRFactoryProducer(items, sharedQueue));
		es.execute(new CapRFactoryConsumer(sharedQueue, progress));
		
		for (int x=1; x<num_threads-1; x++){
			es.execute(new CapRFactoryConsumer(sharedQueue, progress));
		}

		// Make sure threads are GCed once completed
		es.shutdown();
		
		// Wait until all threads are done
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); //wait forever
		} catch (InterruptedException e) {
						
			es.shutdownNow();
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
		}		
		
		
		
		
	}


	public AtomicInteger getProgress() {
		return progress;
	}

	@Override
	public void run() {
		
		predict();
		
		
	}

}
