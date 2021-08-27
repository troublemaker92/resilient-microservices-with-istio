# resilient-microservices-with-istio
bachelor thesis about developing resilient microservices with Istio
kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'
