# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table abstract (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          bigint,
  constraint uq_abstract_text unique (text),
  constraint pk_abstract primary key (id)
);

create table category (
  id                            bigint auto_increment not null,
  version                       integer not null,
  categoryname                  bigint,
  constraint uq_category_categoryname unique (categoryname),
  constraint pk_category primary key (id)
);

create table challenge (
  id                            varchar(255) not null,
  importid                      integer not null,
  mandantid                     bigint,
  isprivate                     tinyint(1) default 0 not null,
  goldnuggettype                varchar(255),
  lastupdate                    datetime(6),
  lastgitcommit                 varchar(255),
  type                          bigint,
  version                       integer not null,
  constraint pk_challenge primary key (id)
);

create table challenge_resources (
  challengeref                  varchar(255) not null,
  resourceref                   varchar(255) not null,
  constraint pk_challenge_resources primary key (challengeref,resourceref)
);

create table challenge_categories (
  challengeref                  varchar(255) not null,
  categoryref                   bigint not null,
  constraint pk_challenge_categories primary key (challengeref,categoryref)
);

create table challenge_keywords (
  challengeref                  varchar(255) not null,
  keywordref                    bigint not null,
  constraint pk_challenge_keywords primary key (challengeref,keywordref)
);

create table challenge_usages (
  challengeref                  varchar(255) not null,
  usageref                      bigint not null,
  constraint pk_challenge_usages primary key (challengeref,usageref)
);

create table challenge_level (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          bigint,
  maxpoint                      integer not null,
  constraint uq_challenge_level_text unique (text),
  constraint pk_challenge_level primary key (id)
);

create table challenge_resource (
  id                            varchar(255) not null,
  version                       integer not null,
  type                          varchar(255),
  constraint pk_challenge_resource primary key (id)
);

create table challenge_status (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          bigint,
  statusorder                   integer not null,
  constraint uq_challenge_status_text unique (text),
  constraint pk_challenge_status primary key (id)
);

create table challenge_type (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          bigint,
  constraint uq_challenge_type_text unique (text),
  constraint pk_challenge_type primary key (id)
);

create table challenge_usage (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          bigint,
  constraint uq_challenge_usage_text unique (text),
  constraint pk_challenge_usage primary key (id)
);

create table challenge_user_execution (
  id                            bigint auto_increment not null,
  version                       integer not null,
  goldnugget                    varchar(255),
  challengeversionid            bigint,
  userid                        bigint,
  constraint pk_challenge_user_execution primary key (id)
);

create table challenge_version (
  id                            bigint auto_increment not null,
  version                       integer not null,
  challengeid                   varchar(255),
  author                        varchar(255),
  created                       datetime(6),
  staticgoldnuggetsecret        varchar(255),
  name                          varchar(255),
  title                         bigint,
  abstractid                    bigint,
  challengelevelid              bigint,
  challengestatusid             bigint,
  solutionid                    bigint,
  maxpoints                     integer not null,
  mediareferenceid              bigint,
  constraint uq_challenge_version_title unique (title),
  constraint uq_challenge_version_abstractid unique (abstractid),
  constraint uq_challenge_version_solutionid unique (solutionid),
  constraint pk_challenge_version primary key (id)
);

create table code_type (
  id                            bigint auto_increment not null,
  version                       integer not null,
  code_name                     varchar(255),
  constraint pk_code_type primary key (id)
);

create table hint (
  id                            varchar(255) not null,
  step_order                    integer not null,
  version                       integer not null,
  sectionid                     varchar(255),
  text                          bigint,
  constraint uq_hint_text unique (text),
  constraint pk_hint primary key (id)
);

create table instruction (
  id                            varchar(255) not null,
  step_order                    integer not null,
  version                       integer not null,
  sectionid                     varchar(255),
  text                          bigint,
  constraint uq_instruction_text unique (text),
  constraint pk_instruction primary key (id)
);

create table issue (
  id                            bigint auto_increment not null,
  version                       integer not null,
  gitid                         varchar(255),
  challengeid                   varchar(255),
  createdby                     bigint,
  constraint uq_issue_createdby unique (createdby),
  constraint pk_issue primary key (id)
);

create table keyword (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          varchar(255),
  constraint pk_keyword primary key (id)
);

create table language_definition (
  id                            bigint auto_increment not null,
  version                       integer not null,
  languagename                  bigint,
  isolangcode                   varchar(255),
  constraint uq_language_definition_languagename unique (languagename),
  constraint pk_language_definition primary key (id)
);

create table mandant_ccs (
  id                            bigint auto_increment not null,
  version                       integer not null,
  name                          varchar(255),
  constraint pk_mandant_ccs primary key (id)
);

create table markdown_media_reference (
  id                            bigint auto_increment not null,
  version                       integer not null,
  markdownid                    varchar(255),
  markdowntype                  varchar(255),
  mediareferenceid              bigint,
  constraint pk_markdown_media_reference primary key (id)
);

create table media_object (
  id                            bigint auto_increment not null,
  version                       integer not null,
  content                       LONGTEXT,
  isolanguagecode               varchar(255),
  mediareferenceid              bigint,
  constraint pk_media_object primary key (id)
);

create table media_reference (
  id                            bigint auto_increment not null,
  version                       integer not null,
  url                           varchar(255),
  constraint pk_media_reference primary key (id)
);

create table message (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          varchar(255),
  datecreated                   date,
  userid                        bigint not null,
  constraint pk_message primary key (id)
);

create table rating (
  id                            bigint auto_increment not null,
  version                       integer not null,
  value                         integer not null,
  text                          varchar(255),
  challengeversionid            bigint,
  createdby                     bigint,
  constraint pk_rating primary key (id)
);

create table role (
  id                            bigint auto_increment not null,
  version                       integer not null,
  rolename                      varchar(255),
  constraint pk_role primary key (id)
);

create table section (
  id                            varchar(255) not null,
  version                       integer not null,
  section_order                 integer not null,
  title                         bigint,
  text                          bigint,
  challengeversionid            bigint,
  constraint uq_section_title unique (title),
  constraint uq_section_text unique (text),
  constraint pk_section primary key (id)
);

create table solution (
  id                            bigint auto_increment not null,
  version                       integer not null,
  text                          bigint,
  constraint uq_solution_text unique (text),
  constraint pk_solution primary key (id)
);

create table translatable_attribute (
  id                            bigint auto_increment not null,
  version                       integer not null,
  codetypeid                    bigint not null,
  constraint pk_translatable_attribute primary key (id)
);

create table user_cas (
  id                            bigint auto_increment not null,
  version                       integer not null,
  name                          varchar(255),
  firstname                     varchar(255),
  lanugageisocode               varchar(255),
  salary                        double not null,
  username                      varchar(255),
  authtoken                     varchar(255),
  constraint pk_user_cas primary key (id)
);

create table user_mandant_role (
  id                            bigint auto_increment not null,
  version                       integer not null,
  roleid                        bigint not null,
  mandantid                     bigint not null,
  userid                        bigint not null,
  constraint pk_user_mandant_role primary key (id)
);

create table attribute_translation (
  id                            bigint auto_increment not null,
  version                       integer not null,
  languageid                    bigint not null,
  attributeid                   bigint not null,
  translation                   LONGTEXT,
  usercreated                   tinyint(1) default 0 not null,
  constraint pk_attribute_translation primary key (id)
);

alter table abstract add constraint fk_abstract_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table category add constraint fk_category_categoryname foreign key (categoryname) references translatable_attribute (id) on delete restrict on update restrict;

alter table challenge add constraint fk_challenge_mandantid foreign key (mandantid) references mandant_ccs (id) on delete restrict on update restrict;
create index ix_challenge_mandantid on challenge (mandantid);

alter table challenge add constraint fk_challenge_type foreign key (type) references translatable_attribute (id) on delete restrict on update restrict;
create index ix_challenge_type on challenge (type);

alter table challenge_resources add constraint fk_challenge_resources_challenge foreign key (challengeref) references challenge (id) on delete restrict on update restrict;
create index ix_challenge_resources_challenge on challenge_resources (challengeref);

alter table challenge_resources add constraint fk_challenge_resources_challenge_resource foreign key (resourceref) references challenge_resource (id) on delete restrict on update restrict;
create index ix_challenge_resources_challenge_resource on challenge_resources (resourceref);

alter table challenge_categories add constraint fk_challenge_categories_challenge foreign key (challengeref) references challenge (id) on delete restrict on update restrict;
create index ix_challenge_categories_challenge on challenge_categories (challengeref);

alter table challenge_categories add constraint fk_challenge_categories_category foreign key (categoryref) references category (id) on delete restrict on update restrict;
create index ix_challenge_categories_category on challenge_categories (categoryref);

alter table challenge_keywords add constraint fk_challenge_keywords_challenge foreign key (challengeref) references challenge (id) on delete restrict on update restrict;
create index ix_challenge_keywords_challenge on challenge_keywords (challengeref);

alter table challenge_keywords add constraint fk_challenge_keywords_keyword foreign key (keywordref) references keyword (id) on delete restrict on update restrict;
create index ix_challenge_keywords_keyword on challenge_keywords (keywordref);

alter table challenge_usages add constraint fk_challenge_usages_challenge foreign key (challengeref) references challenge (id) on delete restrict on update restrict;
create index ix_challenge_usages_challenge on challenge_usages (challengeref);

alter table challenge_usages add constraint fk_challenge_usages_challenge_usage foreign key (usageref) references challenge_usage (id) on delete restrict on update restrict;
create index ix_challenge_usages_challenge_usage on challenge_usages (usageref);

alter table challenge_level add constraint fk_challenge_level_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table challenge_status add constraint fk_challenge_status_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table challenge_type add constraint fk_challenge_type_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table challenge_usage add constraint fk_challenge_usage_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table challenge_user_execution add constraint fk_challenge_user_execution_challengeversionid foreign key (challengeversionid) references challenge_version (id) on delete restrict on update restrict;
create index ix_challenge_user_execution_challengeversionid on challenge_user_execution (challengeversionid);

alter table challenge_user_execution add constraint fk_challenge_user_execution_userid foreign key (userid) references user_cas (id) on delete restrict on update restrict;
create index ix_challenge_user_execution_userid on challenge_user_execution (userid);

alter table challenge_version add constraint fk_challenge_version_challengeid foreign key (challengeid) references challenge (id) on delete restrict on update restrict;
create index ix_challenge_version_challengeid on challenge_version (challengeid);

alter table challenge_version add constraint fk_challenge_version_title foreign key (title) references translatable_attribute (id) on delete restrict on update restrict;

alter table challenge_version add constraint fk_challenge_version_abstractid foreign key (abstractid) references abstract (id) on delete restrict on update restrict;

alter table challenge_version add constraint fk_challenge_version_challengelevelid foreign key (challengelevelid) references challenge_level (id) on delete restrict on update restrict;
create index ix_challenge_version_challengelevelid on challenge_version (challengelevelid);

alter table challenge_version add constraint fk_challenge_version_challengestatusid foreign key (challengestatusid) references challenge_status (id) on delete restrict on update restrict;
create index ix_challenge_version_challengestatusid on challenge_version (challengestatusid);

alter table challenge_version add constraint fk_challenge_version_solutionid foreign key (solutionid) references solution (id) on delete restrict on update restrict;

alter table challenge_version add constraint fk_challenge_version_mediareferenceid foreign key (mediareferenceid) references media_reference (id) on delete restrict on update restrict;
create index ix_challenge_version_mediareferenceid on challenge_version (mediareferenceid);

alter table hint add constraint fk_hint_sectionid foreign key (sectionid) references section (id) on delete restrict on update restrict;
create index ix_hint_sectionid on hint (sectionid);

alter table hint add constraint fk_hint_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table instruction add constraint fk_instruction_sectionid foreign key (sectionid) references section (id) on delete restrict on update restrict;
create index ix_instruction_sectionid on instruction (sectionid);

alter table instruction add constraint fk_instruction_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table issue add constraint fk_issue_challengeid foreign key (challengeid) references challenge (id) on delete restrict on update restrict;
create index ix_issue_challengeid on issue (challengeid);

alter table issue add constraint fk_issue_createdby foreign key (createdby) references user_cas (id) on delete restrict on update restrict;

alter table language_definition add constraint fk_language_definition_languagename foreign key (languagename) references translatable_attribute (id) on delete restrict on update restrict;

alter table markdown_media_reference add constraint fk_markdown_media_reference_mediareferenceid foreign key (mediareferenceid) references media_reference (id) on delete restrict on update restrict;
create index ix_markdown_media_reference_mediareferenceid on markdown_media_reference (mediareferenceid);

alter table media_object add constraint fk_media_object_mediareferenceid foreign key (mediareferenceid) references media_reference (id) on delete restrict on update restrict;
create index ix_media_object_mediareferenceid on media_object (mediareferenceid);

alter table message add constraint fk_message_userid foreign key (userid) references user_cas (id) on delete restrict on update restrict;
create index ix_message_userid on message (userid);

alter table rating add constraint fk_rating_challengeversionid foreign key (challengeversionid) references challenge_version (id) on delete restrict on update restrict;
create index ix_rating_challengeversionid on rating (challengeversionid);

alter table rating add constraint fk_rating_createdby foreign key (createdby) references user_cas (id) on delete restrict on update restrict;
create index ix_rating_createdby on rating (createdby);

alter table section add constraint fk_section_title foreign key (title) references translatable_attribute (id) on delete restrict on update restrict;

alter table section add constraint fk_section_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table section add constraint fk_section_challengeversionid foreign key (challengeversionid) references challenge_version (id) on delete restrict on update restrict;
create index ix_section_challengeversionid on section (challengeversionid);

alter table solution add constraint fk_solution_text foreign key (text) references translatable_attribute (id) on delete restrict on update restrict;

alter table translatable_attribute add constraint fk_translatable_attribute_codetypeid foreign key (codetypeid) references code_type (id) on delete restrict on update restrict;
create index ix_translatable_attribute_codetypeid on translatable_attribute (codetypeid);

alter table user_mandant_role add constraint fk_user_mandant_role_roleid foreign key (roleid) references role (id) on delete restrict on update restrict;
create index ix_user_mandant_role_roleid on user_mandant_role (roleid);

alter table user_mandant_role add constraint fk_user_mandant_role_mandantid foreign key (mandantid) references mandant_ccs (id) on delete restrict on update restrict;
create index ix_user_mandant_role_mandantid on user_mandant_role (mandantid);

alter table user_mandant_role add constraint fk_user_mandant_role_userid foreign key (userid) references user_cas (id) on delete restrict on update restrict;
create index ix_user_mandant_role_userid on user_mandant_role (userid);

alter table attribute_translation add constraint fk_attribute_translation_languageid foreign key (languageid) references language_definition (id) on delete restrict on update restrict;
create index ix_attribute_translation_languageid on attribute_translation (languageid);

alter table attribute_translation add constraint fk_attribute_translation_attributeid foreign key (attributeid) references translatable_attribute (id) on delete restrict on update restrict;
create index ix_attribute_translation_attributeid on attribute_translation (attributeid);


# --- !Downs

alter table abstract drop foreign key fk_abstract_text;

alter table category drop foreign key fk_category_categoryname;

alter table challenge drop foreign key fk_challenge_mandantid;
drop index ix_challenge_mandantid on challenge;

alter table challenge drop foreign key fk_challenge_type;
drop index ix_challenge_type on challenge;

alter table challenge_resources drop foreign key fk_challenge_resources_challenge;
drop index ix_challenge_resources_challenge on challenge_resources;

alter table challenge_resources drop foreign key fk_challenge_resources_challenge_resource;
drop index ix_challenge_resources_challenge_resource on challenge_resources;

alter table challenge_categories drop foreign key fk_challenge_categories_challenge;
drop index ix_challenge_categories_challenge on challenge_categories;

alter table challenge_categories drop foreign key fk_challenge_categories_category;
drop index ix_challenge_categories_category on challenge_categories;

alter table challenge_keywords drop foreign key fk_challenge_keywords_challenge;
drop index ix_challenge_keywords_challenge on challenge_keywords;

alter table challenge_keywords drop foreign key fk_challenge_keywords_keyword;
drop index ix_challenge_keywords_keyword on challenge_keywords;

alter table challenge_usages drop foreign key fk_challenge_usages_challenge;
drop index ix_challenge_usages_challenge on challenge_usages;

alter table challenge_usages drop foreign key fk_challenge_usages_challenge_usage;
drop index ix_challenge_usages_challenge_usage on challenge_usages;

alter table challenge_level drop foreign key fk_challenge_level_text;

alter table challenge_status drop foreign key fk_challenge_status_text;

alter table challenge_type drop foreign key fk_challenge_type_text;

alter table challenge_usage drop foreign key fk_challenge_usage_text;

alter table challenge_user_execution drop foreign key fk_challenge_user_execution_challengeversionid;
drop index ix_challenge_user_execution_challengeversionid on challenge_user_execution;

alter table challenge_user_execution drop foreign key fk_challenge_user_execution_userid;
drop index ix_challenge_user_execution_userid on challenge_user_execution;

alter table challenge_version drop foreign key fk_challenge_version_challengeid;
drop index ix_challenge_version_challengeid on challenge_version;

alter table challenge_version drop foreign key fk_challenge_version_title;

alter table challenge_version drop foreign key fk_challenge_version_abstractid;

alter table challenge_version drop foreign key fk_challenge_version_challengelevelid;
drop index ix_challenge_version_challengelevelid on challenge_version;

alter table challenge_version drop foreign key fk_challenge_version_challengestatusid;
drop index ix_challenge_version_challengestatusid on challenge_version;

alter table challenge_version drop foreign key fk_challenge_version_solutionid;

alter table challenge_version drop foreign key fk_challenge_version_mediareferenceid;
drop index ix_challenge_version_mediareferenceid on challenge_version;

alter table hint drop foreign key fk_hint_sectionid;
drop index ix_hint_sectionid on hint;

alter table hint drop foreign key fk_hint_text;

alter table instruction drop foreign key fk_instruction_sectionid;
drop index ix_instruction_sectionid on instruction;

alter table instruction drop foreign key fk_instruction_text;

alter table issue drop foreign key fk_issue_challengeid;
drop index ix_issue_challengeid on issue;

alter table issue drop foreign key fk_issue_createdby;

alter table language_definition drop foreign key fk_language_definition_languagename;

alter table markdown_media_reference drop foreign key fk_markdown_media_reference_mediareferenceid;
drop index ix_markdown_media_reference_mediareferenceid on markdown_media_reference;

alter table media_object drop foreign key fk_media_object_mediareferenceid;
drop index ix_media_object_mediareferenceid on media_object;

alter table message drop foreign key fk_message_userid;
drop index ix_message_userid on message;

alter table rating drop foreign key fk_rating_challengeversionid;
drop index ix_rating_challengeversionid on rating;

alter table rating drop foreign key fk_rating_createdby;
drop index ix_rating_createdby on rating;

alter table section drop foreign key fk_section_title;

alter table section drop foreign key fk_section_text;

alter table section drop foreign key fk_section_challengeversionid;
drop index ix_section_challengeversionid on section;

alter table solution drop foreign key fk_solution_text;

alter table translatable_attribute drop foreign key fk_translatable_attribute_codetypeid;
drop index ix_translatable_attribute_codetypeid on translatable_attribute;

alter table user_mandant_role drop foreign key fk_user_mandant_role_roleid;
drop index ix_user_mandant_role_roleid on user_mandant_role;

alter table user_mandant_role drop foreign key fk_user_mandant_role_mandantid;
drop index ix_user_mandant_role_mandantid on user_mandant_role;

alter table user_mandant_role drop foreign key fk_user_mandant_role_userid;
drop index ix_user_mandant_role_userid on user_mandant_role;

alter table attribute_translation drop foreign key fk_attribute_translation_languageid;
drop index ix_attribute_translation_languageid on attribute_translation;

alter table attribute_translation drop foreign key fk_attribute_translation_attributeid;
drop index ix_attribute_translation_attributeid on attribute_translation;

drop table if exists abstract;

drop table if exists category;

drop table if exists challenge;

drop table if exists challenge_resources;

drop table if exists challenge_categories;

drop table if exists challenge_keywords;

drop table if exists challenge_usages;

drop table if exists challenge_level;

drop table if exists challenge_resource;

drop table if exists challenge_status;

drop table if exists challenge_type;

drop table if exists challenge_usage;

drop table if exists challenge_user_execution;

drop table if exists challenge_version;

drop table if exists code_type;

drop table if exists hint;

drop table if exists instruction;

drop table if exists issue;

drop table if exists keyword;

drop table if exists language_definition;

drop table if exists mandant_ccs;

drop table if exists markdown_media_reference;

drop table if exists media_object;

drop table if exists media_reference;

drop table if exists message;

drop table if exists rating;

drop table if exists role;

drop table if exists section;

drop table if exists solution;

drop table if exists translatable_attribute;

drop table if exists user_cas;

drop table if exists user_mandant_role;

drop table if exists attribute_translation;

