package org.iplatform.example.ui.feign;

import org.iplatform.example.util.domain.Attach;
import org.iplatform.example.util.domain.AttachQuery;
import org.iplatform.example.util.domain.BootstrapTable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("example-service")
public interface AttachClient {

	@RequestMapping(value = "/exampleservice/api/v1/attach/list", method = RequestMethod.POST)
	public ResponseEntity<BootstrapTable> list(@RequestBody AttachQuery query);
	
	@RequestMapping(value="/exampleservice/api/v1/attach/", method = RequestMethod.PUT)
	public ResponseEntity<Attach> addAttach(@RequestBody Attach attach);
	
	@RequestMapping(value="/exampleservice/api/v1/attach/{fileId}", method = RequestMethod.PUT)
	public ResponseEntity<Attach> updateAttach(@RequestBody Attach attach);
	
	@RequestMapping(value="/exampleservice/api/v1/attach/{fileId}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> deleteAttach(@PathVariable(value = "fileId") String fileId);
	
	@RequestMapping(value="/exampleservice/api/v1/attach/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<Attach> getAttach(@PathVariable(value = "fileId") String fileId);

}
