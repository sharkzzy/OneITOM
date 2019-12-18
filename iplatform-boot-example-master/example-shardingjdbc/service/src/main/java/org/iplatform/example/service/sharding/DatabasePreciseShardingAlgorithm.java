package org.iplatform.example.service.sharding;

import java.util.Collection;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

/**
 * 自定义分库逻辑
 * @author xiaosong
 */
public class DatabasePreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long>{

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
		for(String each : availableTargetNames){
			if (each.endsWith(shardingValue.getValue() % 2 + "")) {
                return each;
            }
		}
		throw new IllegalArgumentException();
	}

}
