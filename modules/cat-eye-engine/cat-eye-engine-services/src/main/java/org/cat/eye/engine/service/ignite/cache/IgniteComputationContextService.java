package org.cat.eye.engine.service.ignite.cache;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.cat.eye.engine.common.model.Computation;
import org.cat.eye.engine.common.model.ComputationState;
import org.cat.eye.engine.common.service.ComputationContextService;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Kotov on 15.03.2019.
 */
public class IgniteComputationContextService implements ComputationContextService {

    private Ignite ignite;

    private IgniteCache<UUID, Computation> computationCache;

    private IgniteCache<UUID, Computation> runningComputationCache;

    public IgniteComputationContextService(String connectionPoints) {

        String[] addresses = connectionPoints.split(",");

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(addresses));

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(ipFinder);

        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setDiscoverySpi(spi);

        cfg.setPeerClassLoadingEnabled(true);
        //Client mode is ON
        cfg.setClientMode(true);

        this.ignite = Ignition.start(cfg);

        this.computationCache = ignite.cache("computation");

        this.runningComputationCache = ignite.cache("runningComputation");
    }

    @Override
    public void storeComputation(Computation computation) {
        this.computationCache.put(computation.getId(), computation);
    }

    @Override
    public Object getArgument(Parameter parameter, String domain) {
        return null;
    }

    @Override
    public void storeArguments(Object[] args, String domain) {

    }

    @Override
    public boolean tryToRunComputation(Computation computation) {

        boolean result =  false;

        try (Transaction tx = ignite.transactions().txStart(
                TransactionConcurrency.PESSIMISTIC,
                TransactionIsolation.REPEATABLE_READ)
        ) {
            if (!runningComputationCache.containsKey(computation.getId())) {
                if (computationCache.containsKey(computation.getId())) {
                    updateComputationState(computation, ComputationState.RUNNING);
                } else {
                    computation.setState(ComputationState.RUNNING);
                    storeComputation(computation);
                }
                runningComputationCache.put(computation.getId(), computation);

                result = true;

                tx.commit();
            } else {
                tx.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }

    private void updateComputationState(Computation computation, ComputationState newState) {

        computation.setState(newState);

        computationCache.invoke(
                computation.getId(),
                (CacheEntryProcessor<UUID, Computation, Boolean>) (mutableEntry, args) -> {
                    Computation tmp = mutableEntry.getValue();
                    tmp.setState(newState);
                    mutableEntry.setValue(tmp);
                    return Boolean.TRUE;
                },
                (Object) null
        );
    }

    @Override
    public Computation addCompletedChildIdAndRefresh(UUID parentId, Computation childComputation) {

        return computationCache.invoke(
                parentId,
                (CacheEntryProcessor<UUID, Computation, Computation>) (mutableEntry, args) -> {
                    Computation tmp = mutableEntry.getValue();
                    tmp.addCompletedChildId(childComputation.getId());
                    mutableEntry.setValue(tmp);
                    return mutableEntry.getValue();
                },
                (Object) null
        );
    }

    @Override
    public void registerChildrenComputations(Computation computation, List<Computation> childComputations) {

        List<UUID> childIDs = childComputations.stream().map(Computation::getId).collect(Collectors.toList());

        computationCache.invoke(
                computation.getId(),
                (CacheEntryProcessor<UUID, Computation, Void>) (mutableEntry, args) -> {
                    Computation tmp = mutableEntry.getValue();
                    tmp.setChildrenIDs(childIDs);
                    tmp.setNextStep(computation.getNextStep() + 1);
                    mutableEntry.setValue(tmp);
                    return null;
                },
                (Object) null
        );
    }

    @Override
    public void fromRunningToWaiting(Computation computation) {
        // set computation status
        updateComputationState(computation, ComputationState.WAITING);
        // remove computation from running set
        runningComputationCache.remove(computation.getId());
    }

    @Override
    public void fromRunningToReady(Computation computation) {
        computationCache.invoke(
                computation.getId(),
                (CacheEntryProcessor<UUID, Computation, Void>) (mutableEntry, args) -> {
                    Computation tmp = mutableEntry.getValue();
                    tmp.setState(ComputationState.READY);
                    tmp.setNextStep(computation.getNextStep() + 1);
                    mutableEntry.setValue(tmp);
                    return null;
                },
                (Object) null
        );
        // remove computation from running set
        runningComputationCache.remove(computation.getId());
    }

    @Override
    public void fromRunningToCompleted(Computation computation) {
        // mark computations as COMPLETED
        updateComputationState(computation, ComputationState.COMPLETED);
        // remove computation from running set
        runningComputationCache.remove(computation.getId());
    }

    @Override
    public void fromWaitingToReady(Computation computation) {
        updateComputationState(computation, ComputationState.READY);
    }

    @Override
    public void close() {

        if (this.computationCache != null) {
            this.computationCache.close();
        }

        if (this.runningComputationCache != null) {
            this.runningComputationCache.close();
        }

        if (this.ignite != null) {
            this.ignite.close();
        }
    }

    public Computation getComputation(UUID id) {
        return computationCache.get(id);
    }

    public Computation getRunningComputation(UUID id) {
        return runningComputationCache.get(id);
    }

}
