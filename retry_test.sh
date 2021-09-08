export MINIKUBE_IP=$(minikube ip)
export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
kubectl apply -f k8s/billing-deployment.yaml
kubectl apply -f k8s/control-center-deployment.yaml
kubectl apply -f k8s/retry_calculation-deployment.yaml
kubectl apply -f istio/ing_gateway.yaml
kubectl apply -f istio/virt_svc.yaml
kubectl apply -f istio/retry.yaml
echo "Waiting 20 seconds till all services are up and running..."
sleep 20;
curl --location --request POST "http://$MINIKUBE_IP:30033/fault?timeout=0&responseCode=502&frequency=2&shouldCalculate=false"
printf "\n";
curl --location --request POST "http://$MINIKUBE_IP:$INGRESS_PORT/createAccount?user=ruslan" --data-raw ''
curl --location --request POST "http://$MINIKUBE_IP:$INGRESS_PORT/depositAccount?user=ruslan&amount=150000"
for i in {1..20}; do sleep 0.1; curl --location --request POST "http://$MINIKUBE_IP:$INGRESS_PORT/calculate2" --header 'Content-Type: application/json' --data-raw '{"user": "ruslan","calculationType": "prime","price": 15,"firstNumber":  120,"secondNumber": 200}'; done
printf "\n";
echo "Result: "
curl --location --request GET "http://$MINIKUBE_IP:30033/actuator/httptrace"