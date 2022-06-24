Brukernavn/passord for bruker mot STS

* Opprette bruker i gruppetilgang-servicebruker:     
  https://github.com/navikt/gruppetilgang-servicebruker/pull/1049
* Denne manglet et par grupper:   
  https://github.com/navikt/gruppetilgang-servicebruker/pull/1072
  og https://github.com/navikt/gruppetilgang-servicebruker/pull/1074
* Legge til de samme gruppene i prod:  
  https://github.com/navikt/gruppetilgang-servicebruker/pull/1112
* Endre så innslag i Vault mappes opp i tiltakspenger-arena:  
  https://github.com/navikt/vault-iac/pull/4629
  (Her er det muligens gjort noe annet også tidligere..)

* Opprette secret i Kubernetes i dev-fss:   
  `kubectl create secret generic srvtpts-arena --from-literal=SERVICEUSER_TPTS_USERNAME=<username>
  --from-literal=SERVICEUSER_TPTS_PASSWORD=<hemmeligheten> --namespace tpts`
* Opprette secret i Kubernetes i prod-fss:   
  `kubectl create secret generic srvtpts-arena --from-literal=SERVICEUSER_TPTS_USERNAME=<username>
  --from-literal=SERVICEUSER_TPTS_PASSWORD=<hemmeligheten> --namespace tpts`

ClientId/Secret for ORDS tjeneste oppfoelging/aktiviteter i Arena

* Opprette clientId/secret:  
  https://jira.adeo.no/browse/ARENA-7441
* Opprette secret i Kubernetes:    
  `kubectl create secret generic ords-arena --from-literal=ARENA_ORDS_CLIENT_ID=<id> --from-literal=ARENA_ORDS_CLIENT_SECRET=<secret> --namespace tpts`
