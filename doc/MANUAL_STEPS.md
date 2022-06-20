Brukernavn/passord for bruker mot STS

* Opprette bruker:
  ...
* Opprette secret i Kubernetes:   
  `kubectl create secret generic srvtpts-arena --from-literal=SERVICEUSER_TPTS_USERNAME=<username>
  --from-literal=SERVICEUSER_TPTS_PASSWORD=<hemmeligheten> --namespace tpts`
* Opprett secret i Kubernetes:    
  `kubectl create secret generic ords-arena --from-literal=CLIENT_ID=<id> --from-literal=CLIENT_SECRET=<secret> --namespace tpts`
