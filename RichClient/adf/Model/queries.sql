-- query matches

---------- ------ ------------ ------------ ---------- ---------- -------------- -------------- ------------------------------ -------------- ------------------------------ -------------- ------------------------------- -------------------- ----------
 select  mr.id 
  ,      mr.group1
  ,      t1.country home_country
  ,      t2.country away_country
  ,      mr.home_goals
  ,      mr.away_goals
  ,      t1.fifa_rank home_fifa_rank
  ,      t2.fifa_rank away_fifa_rank
  ,      r1.name home_region
  ,      r1.continent home_continent
  ,      r2.name away_region
  ,      r2.continent away_continent
  ,      mr.local_start_time
  ,      mr.scoring_process
  ,      sdm.lattitude
  from   wc_match_results mr
         join
         wc_stadiums sdm
         on (mr.sdm_id = sdm.id)
         join
         wc_teams t1
         on ( mr.home_team_id = t1.id )
         join
         wc_teams t2
         on ( mr.away_team_id = t2.id
            )
         join 
         wc_football_regions r1
         on (t1.rgn_id = r1.id)
         join 
         wc_football_regions r2
          on (t2.rgn_id = r2.id)
order by group1, id



-- query group standings
-------------------

with results as
( select mr.group1
, mr.home_team_id
, mr.home_goals
, mr.away_team_id
, mr.away_goals
from wc_match_results mr
union all
select mr.group1
, mr.away_team_id
, mr.away_goals
, mr.home_team_id
, mr.home_goals
from wc_match_results mr
)
, team_results as
( select group1
, home_team_id team_id
, count(*) over (partition by group1, home_team_id) games_played
, sum( case
when home_goals > away_goals then 3
when away_goals > home_goals then 0
else 1 end
) over (partition by group1, home_team_id) points
, sum( home_goals) over (partition by group1, home_team_id) goals_scored
, sum( away_goals) over (partition by group1, home_team_id) goals_conceded
from results
)
select distinct
tr.group1
, t.country
, games_played
, points
, goals_scored-goals_conceded goal_difference
, goals_scored
from team_results tr
join
wc_teams t
on (tr.team_id = t.id
)
order
by tr.group1
, points desc
, goal_difference desc
, goals_scored desc













create or replace type string_table as table of varchar2(500);


overall tag cloud

 with matches as
(  select  mr.id 
  ,      mr.group1
  ,      t1.country home_country
  ,      t2.country away_country
  ,      mr.home_goals
  ,      mr.away_goals
  ,      t1.fifa_rank home_fifa_rank
  ,      t2.fifa_rank away_fifa_rank
  ,      r1.name home_region
  ,      r1.continent home_continent
  ,      r2.name away_region
  ,      r2.continent away_continent
  ,      mr.local_start_time
  ,      mr.scoring_process
  ,      sdm.lattitude
  from   wc_match_results mr
         join
         wc_stadiums sdm
         on (mr.sdm_id = sdm.id)
         join
         wc_teams t1
         on ( mr.home_team_id = t1.id )
         join
         wc_teams t2
         on ( mr.away_team_id = t2.id
            )
         join 
         wc_football_regions r1
         on (t1.rgn_id = r1.id)
         join 
         wc_football_regions r2
          on (t2.rgn_id = r2.id))
, scoring_process (match_id, intermediate_score, iteration) as
( select id, substr(scoring_process,1,1), 1
  from   wc_match_results
  union all
  select id,substr(scoring_process,1,iteration+1), iteration+1
  from   wc_match_results mr
         join
         scoring_process sp
         on (sp.match_id = mr.id and length(scoring_process)>= iteration + 1)
)
, tagged as
(select t.tag
 ,      mt.match_id
 from   wc_match_tags mt
        join
        wc_tags t
        on (mt.tag_id = t.id)
 union all
 select 'Surprise' tag
 ,      m.id match_id
 from   matches m
 where (m.home_goals > m.away_goals and m.home_fifa_rank - m.away_fifa_rank > 5)
        or 
       (m.home_goals < m.away_goals and m.home_fifa_rank - m.away_fifa_rank < -5)
        or 
       (m.home_goals = m.away_goals and abs(m.home_fifa_rank - m.away_fifa_rank)  > 10)
 union all      
 select 'Intercontinental' tag
 ,      m.id match_id
 from   matches m
 where (m.home_continent != m.away_continent)
 union all      
 select 'Derby' tag
 ,        m.id match_id
 from   matches m
 where (m.home_region = m.away_region)
 union all      
 select case
        when lattitude < 10 then 'north'
        when lattitude > 23 then 'south'
        else 'central' end tag
 ,      m.id match_id
 from   matches m
 union all      
 select  case to_char(m.LOCAL_START_TIME, 'HH24') 
         when '13' then 'early' 
         when '22' then 'late'
         end tag
 ,       m.id match_id
 from    matches m
 where   to_char(m.LOCAL_START_TIME, 'HH24') in ('13','22')
 union all      
 select   'goalless tie' tag
 ,       m.id match_id
 from    matches m
 where   home_goals + away_goals=0
 union all 
 select  case 
         when id < 49 then 'group stage'
         when id > 48 then 'knock out' 
         end tag
 ,        id
 from    matches m
 union all      
 select  'recent' tag
 ,       m.id match_id
 from    ( select m.* 
           ,      row_number() over (order by m.local_start_time desc) rnk
           from   matches m
        ) m
 where   rnk < 6
 union all      
 select  'exciting' tag
 ,        m.id match_id
 from     matches m
 where    home_goals + away_goals > 4
 union all 
 select  'comeback' tag 
 ,        match_id 
 from     ( select ms.match_id 
            from   ( select sp.* 
                     ,      length(translate('1'||intermediate_score, '10','1')) - length(translate('0'||intermediate_score, '01','0')) running_score 
                     from   scoring_process sp 
                   ) ms          
            having max(running_score)> 0 and min(running_score) < 0 
            group 
            by     ms.match_id 
          )         
)
select substr(tag, 1, 25) tag
,      count(match_id) occurrences
from   tagged
group
by     substr(tag, 1, 25)
order
by     occurrences
  
    
TAG CLOUD per match:
 with matches as
(  select  mr.id 
  ,      mr.group1
  ,      t1.country home_country
  ,      t2.country away_country
  ,      mr.home_goals
  ,      mr.away_goals
  ,      t1.fifa_rank home_fifa_rank
  ,      t2.fifa_rank away_fifa_rank
  ,      r1.name home_region
  ,      r1.continent home_continent
  ,      r2.name away_region
  ,      r2.continent away_continent
  ,      mr.local_start_time
  ,      mr.scoring_process
  ,      sdm.lattitude
  from   wc_match_results mr
         join
         wc_stadiums sdm
         on (mr.sdm_id = sdm.id)
         join
         wc_teams t1
         on ( mr.home_team_id = t1.id )
         join
         wc_teams t2
         on ( mr.away_team_id = t2.id
            )
         join 
         wc_football_regions r1
         on (t1.rgn_id = r1.id)
         join 
         wc_football_regions r2
          on (t2.rgn_id = r2.id))
, scoring_process (match_id, intermediate_score, iteration) as
( select id, substr(scoring_process,1,1), 1
  from   wc_match_results
  union all
  select id,substr(scoring_process,1,iteration+1), iteration+1
  from   wc_match_results mr
         join
         scoring_process sp
         on (sp.match_id = mr.id and length(scoring_process)>= iteration + 1)
)
, tagged as
(select t.tag
 ,      mt.match_id
 from   wc_match_tags mt
        join
        wc_tags t
        on (mt.tag_id = t.id)
 union all
 select 'Surprise' tag
 ,      m.id match_id
 from   matches m
 where (m.home_goals > m.away_goals and m.home_fifa_rank - m.away_fifa_rank > 5)
        or 
       (m.home_goals < m.away_goals and m.home_fifa_rank - m.away_fifa_rank < -5)
        or 
       (m.home_goals = m.away_goals and abs(m.home_fifa_rank - m.away_fifa_rank)  > 10)
 union all      
 select 'Intercontinental' tag
 ,      m.id match_id
 from   matches m
 where (m.home_continent != m.away_continent)
 union all      
 select 'Derby' tag
 ,        m.id match_id
 from   matches m
 where (m.home_region = m.away_region)
 union all      
 select case
        when lattitude < 10 then 'north'
        when lattitude > 23 then 'south'
        else 'central' end tag
 ,      m.id match_id
 from   matches m
 union all      
 select  case to_char(m.LOCAL_START_TIME, 'HH24') 
         when '13' then 'early' 
         when '22' then 'late'
         end tag
 ,       m.id match_id
 from    matches m
 where   to_char(m.LOCAL_START_TIME, 'HH24') in ('13','22')
 union all      
 select   'goalless tie' tag
 ,       m.id match_id
 from    matches m
 where   home_goals + away_goals=0
 union all      
 select  'recent' tag
 ,       m.id match_id
 from    ( select m.* 
           ,      row_number() over (order by m.local_start_time desc) rnk
           from   matches m
        ) m
 where   rnk < 6
 union all      
 select  'exciting' tag
 ,        m.id match_id
 from     matches m
 where    home_goals + away_goals > 4
 union all 
 select  'comeback' tag 
 ,        match_id 
 from     ( select ms.match_id 
            from   ( select sp.* 
                     ,      length(translate('1'||intermediate_score, '10','1')) - length(translate('0'||intermediate_score, '01','0')) running_score 
                     from   scoring_process sp 
                   ) ms          
            having max(running_score)> 0 and min(running_score) < 0 
            group 
            by     ms.match_id 
          )         
)
select home_country ||'-'||away_country lineup
,      home_goals ||'-'||away_goals score
,      tags
from   ( select match_id
         ,      listagg(lower(tag),',') within group (order by tag) tags
         from   tagged
         group
         by     match_id
       ) mt
       join
       matches m
       on (m.id = mt.match_id)






 with  tag_filter as
 ( select string_tbl('recent','comeback') selected_tags
   from   dual
 )
 , filter_tags as
 ( select column_value filter_tag
   from   table((select selected_tags from tag_filter))
 )   
select * 
from filter_tags


FILTER_TAG                                       
--------------------------------------------------
recent                                             
comeback       



filter out matches that do not have all matching tags
filter out selected tags

 with  tag_filter as
 ( select string_table('north','comeback') selected_tags
   from   dual
 )
 , filter_tags as
 ( select column_value filter_tag
   from   table((select selected_tags from tag_filter))
 )   
 , matches as
(  select  mr.id 
  ,      mr.group1
  ,      t1.country home_country
  ,      t2.country away_country
  ,      mr.home_goals
  ,      mr.away_goals
  ,      t1.fifa_rank home_fifa_rank
  ,      t2.fifa_rank away_fifa_rank
  ,      r1.name home_region
  ,      r1.continent home_continent
  ,      r2.name away_region
  ,      r2.continent away_continent
  ,      mr.local_start_time
  ,      mr.scoring_process
  ,      sdm.lattitude
  from   wc_match_results mr
         join
         wc_stadiums sdm
         on (mr.sdm_id = sdm.id)
         join
         wc_teams t1
         on ( mr.home_team_id = t1.id )
         join
         wc_teams t2
         on ( mr.away_team_id = t2.id
            )
         join 
         wc_football_regions r1
         on (t1.rgn_id = r1.id)
         join 
         wc_football_regions r2
          on (t2.rgn_id = r2.id))
, scoring_process (match_id, intermediate_score, iteration) as
( select id, substr(scoring_process,1,1), 1
  from   wc_match_results
  union all
  select id,substr(scoring_process,1,iteration+1), iteration+1
  from   wc_match_results mr
         join
         scoring_process sp
         on (sp.match_id = mr.id and length(scoring_process)>= iteration + 1)
)
, tagged as
(select t.tag
 ,      mt.match_id
 from   wc_match_tags mt
        join
        wc_tags t
        on (mt.tag_id = t.id)
 union all
 select 'Surprise' tag
 ,      m.id match_id
 from   matches m
 where (m.home_goals > m.away_goals and m.home_fifa_rank - m.away_fifa_rank > 5)
        or 
       (m.home_goals < m.away_goals and m.home_fifa_rank - m.away_fifa_rank < -5)
        or 
       (m.home_goals = m.away_goals and abs(m.home_fifa_rank - m.away_fifa_rank)  > 10)
 union all      
 select 'Intercontinental' tag
 ,      m.id match_id
 from   matches m
 where (m.home_continent != m.away_continent)
 union all      
 select 'Derby' tag
 ,        m.id match_id
 from   matches m
 where (m.home_region = m.away_region)
 union all      
 select case
        when lattitude < 10 then 'north'
        when lattitude > 23 then 'south'
        else 'central' end tag
 ,      m.id match_id
 from   matches m
 union all      
 select  case to_char(m.LOCAL_START_TIME, 'HH24') 
         when '13' then 'early' 
         when '22' then 'late'
         end tag
 ,       m.id match_id
 from    matches m
 where   to_char(m.LOCAL_START_TIME, 'HH24') in ('13','22')
 union all      
 select   'goalless tie' tag
 ,       m.id match_id
 from    matches m
 where   home_goals + away_goals=0
 union all      
 select  'recent' tag
 ,       m.id match_id
 from    ( select m.* 
           ,      row_number() over (order by m.local_start_time desc) rnk
           from   matches m
        ) m
 where   rnk < 6
 union all      
 select  'exciting' tag
 ,        m.id match_id
 from     matches m
 where    home_goals + away_goals > 4
 union all 
 select  'comeback' tag 
 ,        match_id 
 from     ( select ms.match_id 
            from   ( select sp.* 
                     ,      length(translate('1'||intermediate_score, '10','1')) - length(translate('0'||intermediate_score, '01','0')) running_score 
                     from   scoring_process sp 
                   ) ms          
            having max(running_score)> 0 and min(running_score) < 0 
            group 
            by     ms.match_id 
          )         
)
, match_tag_sets as
( select match_id
  ,      cast(collect(substr(tag,1,25)) as string_table) tagset     
  from   tagged
         join
         matches m
         on (m.id = tagged.match_id)
  group 
  by     match_id
)
, selected_matches as
( select match_id
  from   match_tag_sets mts
         cross join
         tag_filter  tf     
  where  tf.selected_tags SUBMULTISET  mts.tagset
)
select home_country ||'-'||away_country lineup
,      home_goals ||'-'||away_goals score
,      tags
from   ( select match_id
         ,      listagg(lower(tag),',') within group (order by tag) tags
         from   tagged
                join
                selected_matches
                using (match_id)   
         cross join
         tag_filter  tf     
         where  tag not member of tf.selected_tags 
         group
         by     match_id
       ) mt
       join
       matches m
       on (m.id = mt.match_id)


LINEUP  SCORE                                                                             TAGS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
------- --------------------------------------------------------------------------------- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
AUS-NLD 2-3                                                                               intercontinental,early,exciting,south,super goal                  


select tag cloud for remaining matches - leaving out the selected tags:

 with  tag_filter as
 ( select string_table('Intercontinental','north') selected_tags
   from   dual
 )
 , filter_tags as
 ( select column_value filter_tag
   from   table((select selected_tags from tag_filter))
 )   
 , matches as
(  select  mr.id 
  ,      mr.group1
  ,      t1.country home_country
  ,      t2.country away_country
  ,      mr.home_goals
  ,      mr.away_goals
  ,      t1.fifa_rank home_fifa_rank
  ,      t2.fifa_rank away_fifa_rank
  ,      r1.name home_region
  ,      r1.continent home_continent
  ,      r2.name away_region
  ,      r2.continent away_continent
  ,      mr.local_start_time
  ,      mr.scoring_process
  ,      sdm.lattitude
  from   wc_match_results mr
         join
         wc_stadiums sdm
         on (mr.sdm_id = sdm.id)
         join
         wc_teams t1
         on ( mr.home_team_id = t1.id )
         join
         wc_teams t2
         on ( mr.away_team_id = t2.id
            )
         join 
         wc_football_regions r1
         on (t1.rgn_id = r1.id)
         join 
         wc_football_regions r2
          on (t2.rgn_id = r2.id))
, scoring_process (match_id, intermediate_score, iteration) as
( select id, substr(scoring_process,1,1), 1
  from   wc_match_results
  union all
  select id,substr(scoring_process,1,iteration+1), iteration+1
  from   wc_match_results mr
         join
         scoring_process sp
         on (sp.match_id = mr.id and length(scoring_process)>= iteration + 1)
)
, tagged as
(select t.tag
 ,      mt.match_id
 from   wc_match_tags mt
        join
        wc_tags t
        on (mt.tag_id = t.id)
 union all
 select 'Surprise' tag
 ,      m.id match_id
 from   matches m
 where (m.home_goals > m.away_goals and m.home_fifa_rank - m.away_fifa_rank > 5)
        or 
       (m.home_goals < m.away_goals and m.home_fifa_rank - m.away_fifa_rank < -5)
        or 
       (m.home_goals = m.away_goals and abs(m.home_fifa_rank - m.away_fifa_rank)  > 10)
 union all      
 select 'Intercontinental' tag
 ,      m.id match_id
 from   matches m
 where (m.home_continent != m.away_continent)
 union all      
 select 'Derby' tag
 ,        m.id match_id
 from   matches m
 where (m.home_region = m.away_region)
 union all      
 select case
        when lattitude < 10 then 'north'
        when lattitude > 23 then 'south'
        else 'central' end tag
 ,      m.id match_id
 from   matches m
 union all      
 select  case to_char(m.LOCAL_START_TIME, 'HH24') 
         when '13' then 'early' 
         when '22' then 'late'
         end tag
 ,       m.id match_id
 from    matches m
 where   to_char(m.LOCAL_START_TIME, 'HH24') in ('13','22')
 union all      
 select   'goalless tie' tag
 ,       m.id match_id
 from    matches m
 where   home_goals + away_goals=0
 union all      
 select  'recent' tag
 ,       m.id match_id
 from    ( select m.* 
           ,      row_number() over (order by m.local_start_time desc) rnk
           from   matches m
        ) m
 where   rnk < 6
 union all      
 select  'exciting' tag
 ,        m.id match_id
 from     matches m
 where    home_goals + away_goals > 4
 union all 
 select  'comeback' tag 
 ,        match_id 
 from     ( select ms.match_id 
            from   ( select sp.* 
                     ,      length(translate('1'||intermediate_score, '10','1')) - length(translate('0'||intermediate_score, '01','0')) running_score 
                     from   scoring_process sp 
                   ) ms          
            having max(running_score)> 0 and min(running_score) < 0 
            group 
            by     ms.match_id 
          )         
)
, match_tag_sets as
( select match_id
  ,      cast(collect(substr(tag,1,25)) as string_table) tagset     
  from   tagged
         join
         matches m
         on (m.id = tagged.match_id)
  group 
  by     match_id
)
, selected_matches as
( select match_id
  from   match_tag_sets mts
         cross join
         tag_filter  tf     
  where  tf.selected_tags SUBMULTISET  mts.tagset
)
select substr(tag, 1, 25) tag
,      count(match_id) occurrences
from   tagged
       join
       selected_matches
       using (match_id)   
       cross join
       tag_filter  tf     
where  tag not member of tf.selected_tags 
group
by     substr(tag, 1, 25)
order
by     occurrences desc




 with  
   function string_tokenizer( p_string in varchar2, p_token in varchar2 default ',')
   return   string_tbl
   is
      l_token   VARCHAR2(4) ;
      i          PLS_INTEGER := 1 ;  
     l_tokens string_tbl := string_tbl();
     FUNCTION get_token(
        p_input_string IN VARCHAR2,            -- input string
        p_token_number IN PLS_INTEGER,         -- token number
        p_delimiter    IN VARCHAR2 DEFAULT ',' -- separator character
     )
       RETURN VARCHAR2
     IS
  l_temp_string VARCHAR2(32767) := p_delimiter || p_input_string ;
  l_pos1 number ;
  l_pos2 number ;
BEGIN
  l_pos1     := INSTR( l_temp_string, p_delimiter, 1, p_token_number ) ;
  IF l_pos1   > 0 THEN
    l_pos2   := INSTR( l_temp_string, p_delimiter, 1, p_token_number + 1) ;
    IF l_pos2 = 0 THEN
      l_pos2 := LENGTH( l_temp_string ) + 1 ;
    END IF ;
    RETURN( SUBSTR( l_temp_string, l_pos1+1, l_pos2 - l_pos1-1 ) ) ;
  ELSE
    RETURN NULL ;
  END IF ;
END get_token;
    BEGIN
      LOOP
        l_token := get_token( p_string, i , ',') ;
        EXIT WHEN l_token IS NULL ;
        l_tokens.extend;
        l_tokens(l_tokens.last) := l_token;
        i := i + 1 ;
     END LOOP ;
     return l_tokens;
   end;

select string_tokenizer( 'ding,dong') from dual;   
 