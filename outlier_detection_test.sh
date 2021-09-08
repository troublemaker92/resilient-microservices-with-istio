export MINIKUBE_IP=$(minikube ip)
export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
kubectl apply -f k8s/billing-deployment.yaml
kubectl apply -f k8s/control-center-deployment.yaml
kubectl apply -f k8s/calculation-deployment.yaml
kubectl apply -f k8s/analytics-deployment.yaml
kubectl apply -f istio/ing_gateway.yaml
kubectl apply -f istio/virt_svc_10_90_canary.yaml
kubectl apply -f istio/virt_svc.yaml
kubectl apply -f istio/outlier_detection.yaml
echo "Waiting 50 seconds till all services are up and running..."
sleep 50;
curl --location --request POST "http://$MINIKUBE_IP:30033/fault?timeout=0&responseCode=500&frequency=0&shouldCalculate=false"
printf "\n"
curl --location --request GET "http://localhost:8080/simulate?duration=210&ip=$MINIKUBE_IP&port=$INGRESS_PORT" --data-raw ''