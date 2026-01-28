SELECT
    JSON_OBJECT(
          'transaksjonsdato' VALUE to_char(vuh.dato_postert,gk_json_datoformat)
        , 'transaksjonstypenavn' VALUE vuh.transaksjonstype
        , 'periode' VALUE JSON_OBJECT ('fom' VALUE to_char(vuh.dato_periode_fra,gk_json_datoformat),
                                       'tom' VALUE to_char(vuh.dato_periode_til,gk_json_datoformat))
        , 'meldekortId' VALUE vuh.meldekort_id
        , 'beloep' VALUE vuh.belop
        , 'sats' VALUE vuh.posteringsats
        , 'status' VALUE vuh.status
        , 'anmerkninger' VALUE (SELECT
                                    JSON_ARRAYAGG(
                                            JSON_OBJECT (
                                                'beskrivelse' VALUE REPLACE(anme.beskrivelse, k_parameterverdi, anme.verdi)
                                                , 'kilde' VALUE case when anme.vedtak_id IS NOT NULL THEN 'Vedtak' ELSE 'Meldekort'
                                                END
                                            )
                                    )
                                FROM v_api_anmerkning anme
                                WHERE (anme.objekt_id = vuh.objekt_id_kilde
                                    AND anme.tabellnavnalias = vuh.tabellnavnalias_kilde
                                    AND anme.tabellnavnalias = 'MKORT'
                                    AND (anme.vedtak_id   = vuh.v_id
                                        OR anme.vedtak_id  IS NULL )
                                    AND  UPPER( vuh.transaksjonstype ) NOT LIKE '%FORSKUDD%')
                 )
        , 'vedtaksfakta' VALUE (SELECT
                                    JSON_ARRAYAGG (
                                            JSON_OBJECT (
                                                'navn' VALUE vefa.vedtakfaktanavn
                                                ,'verdi' VALUE vefa.vedtakverdi
                                            )
                                    )
                                FROM v_api_vedtakfakta vefa
                                WHERE vuh.vedtak_id = vefa.vedtak_id)
    ) innhold
FROM v_api_utbethist vuh
WHERE vuh.person_id = p_parameter.person_id
  AND vuh.dato_periode_fra <= p_parameter.periode_tom
  AND vuh.dato_periode_til >= p_parameter.periode_fom
ORDER BY vuh.dato_postert;
