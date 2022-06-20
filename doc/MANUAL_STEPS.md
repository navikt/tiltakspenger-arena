Brukernavn/passord for bruker mot STS

* Opprette bruker i gruppetilgang-servicebruker:     
  https://github.com/navikt/gruppetilgang-servicebruker/pull/1049
* Denne manglet et par grupper:   
  https://github.com/navikt/gruppetilgang-servicebruker/pull/1072 og https://github.com/navikt/gruppetilgang-servicebruker/pull/1074
* Opprette secret i Kubernetes:   
  `kubectl create secret generic srvtpts-arena --from-literal=SERVICEUSER_TPTS_USERNAME=<username>
  --from-literal=SERVICEUSER_TPTS_PASSWORD=<hemmeligheten> --namespace tpts`

ClientId/Secret for ORDS tjeneste oppfoelging/aktiviteter i Arena

* Opprette clientId/secret:  
  https://jira.adeo.no/browse/ARENA-7441
* Opprette secret i Kubernetes:    
  `kubectl create secret generic ords-arena --from-literal=ARENA_ORDS_CLIENT_ID=<id> --from-literal=ARENA_ORDS_CLIENT_SECRET=<secret> --namespace tpts`
