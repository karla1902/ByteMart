"""empty message

Revision ID: 313585962b5c
Revises: a02c8723e8aa
Create Date: 2024-10-02 21:49:15.860530

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import mysql

# revision identifiers, used by Alembic.
revision = '313585962b5c'
down_revision = 'a02c8723e8aa'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('carrito',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('usuario_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['usuario_id'], ['usuario.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('item_carrito',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('carrito_id', sa.Integer(), nullable=False),
    sa.Column('producto_id', sa.Integer(), nullable=False),
    sa.Column('cantidad', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['carrito_id'], ['carrito.id'], ),
    sa.ForeignKeyConstraint(['producto_id'], ['producto.id'], ),
    sa.PrimaryKeyConstraint('id')
    )
    with op.batch_alter_table('producto', schema=None) as batch_op:
        batch_op.alter_column('price',
               existing_type=mysql.VARCHAR(length=100),
               type_=sa.Integer(),
               existing_nullable=False)

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('producto', schema=None) as batch_op:
        batch_op.alter_column('price',
               existing_type=sa.Integer(),
               type_=mysql.VARCHAR(length=100),
               existing_nullable=False)

    op.drop_table('item_carrito')
    op.drop_table('carrito')
    # ### end Alembic commands ###
